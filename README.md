# Minibank API

A small banking backend used as a technical exercise.  
The goal is to expose a simple REST API to **create** and **list** bank accounts, using **Spring Boot**, **Java 21**, and an **H2 in‑memory database**.

## Stack & Requirements

- **Language**: Java 21+
- **Framework**: Spring Boot
- **Build Tool**: Maven
- **Database**: H2 (in-memory)

Default HTTP port: **`8080`**

## Running the Application

### 1. Prerequisites

- **Java**: 21+
- **Maven**: 3.8+ (or use Maven wrapper, if present)
- **Optional**: Docker (if you want to run external DBs or other services)

### 2. Clone the Repository

```bash
git clone https://github.com/felurye/mini-bank.git
cd mini-bank
```

### 3. Run with Maven

```bash
mvn spring-boot:run
```

The API will be available at:

- `http://localhost:8080`

### 4. H2 Console

H2 is configured as an in-memory database.

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

You can use the console to inspect the `ACCOUNT` table during development.

## Building & Running Tests

### Run all tests

```bash
mvn test
```

This executes:

- Unit tests for:
  - `AccountMapper`
  - `AccountService`

## API Overview

### 1. Create account

- **Method**: `POST`
- **URL**: `/accounts`
- **Request body**:

```json
{
  "name": "User Name",
  "cpf": "12345678901"
}
```

- **Success response**:
  - Status: **`201 Created`**
  - Body: currently **empty** (can be extended to return the created account and/or `Location` header)

- **Validation rules**:
  - `name`: required (`@NotBlank`)
  - `cpf`: required (`@NotBlank`)
  - `cpf`: must be **unique** (no other account with the same CPF)

- **Error cases**:
  - Missing/blank fields → `400 Bad Request` with a structured error payload.
  - CPF already registered → `409 Conflict` with a structured error payload.

### 2. List accounts

- **Method**: `GET`
- **URL**: `/accounts`
- **Success response**:
  - Status: **`200 OK`**
  - Body: JSON array of account DTOs:

```json
[
  {
    "id": 1,
    "name": "User Name",
    "cpf": "12345678901"
  }
]
```

### CORS

The controller is annotated with `@CrossOrigin`, allowing the React frontend to call this API from a different origin.
For a real production system you would restrict origins, but for the exercise the configuration is intentionally permissive.

## Architecture

The project follows a layered architecture with clear separation between **API**, **domain**, and **infrastructure** concerns.

### Package structure

- `com.minibank`
  - `MinibankApplication` – Spring Boot main class.
- `com.minibank.api`
  - `controller` – REST controllers (`AccountController`).
  - `request` – Input DTOs from the outside world (`AccountRequest`).
  - `response` – Output DTOs (`AccountResponse`).
  - `mapper` – Mapping between API DTOs and domain entities (`AccountMapper`).
  - `exception` – API‑level exceptions and global exception handler (if present).
- `com.minibank.domain`
  - `entitys` – JPA entities (`Account`).
  - `repository` – Spring Data JPA repositories (`AccountRepository`).
  - `service` – Business services (`AccountService`).
- `com.minibank.config`
  - Beans and cross‑cutting configuration (e.g. `ModelMapper` bean).

## Technical Decisions

### 1. H2 in-memory database

- **Why**: Fast, zero‑setup database perfectly suitable for a coding exercise.
- **Benefit**: No external dependency; the backend runs with a single `mvn spring-boot:run`.
- **Trade-off**: Data is lost on restart. For production, a persistent relational DB (e.g., Postgres) would be used.

### 2. Layered architecture (Controller → Service → Repository)

- **Why**:
  - Keep business rules in the **service layer**.
  - Keep controllers thin and focused purely on HTTP (status codes, DTOs, etc.).
  - Use repositories only for persistence concerns.
- **Benefit**:
  - Easier to test each layer in isolation (unit tests for services and mappers, integration tests for controllers).
  - Clear separation of concerns; easier to extend during the interview.

### 3. DTOs and ModelMapper

- **Why**:
  - Avoid exposing JPA entities directly as API contracts (better encapsulation).
  - Keep mapping logic centralized in `AccountMapper`.
  - Use ModelMapper to reduce boilerplate for simple property‑to‑property mappings.
- **Trade-off**:
  - Another dependency, but trivial to replace with manual mapping if desired.

### 4. Validation with Jakarta Bean Validation

- **Why**:
  - Declarative and concise way to validate request payloads.
  - Standard approach in Spring Boot applications.
- **Implementation**:
  - `@NotBlank` on `name` and `cpf`.
  - `@Valid` on controller method parameters.
  - A global exception handler (optional) to transform validation errors into a consistent JSON shape.

### 5. Duplicate CPF rule at the service layer

- **Why**:
  - Business rule (“CPF must be unique”) belongs in the **domain/service layer**, not directly in the controller.
  - Makes the rule testable independent of HTTP.
- **Implementation**:
  - `AccountService.saveAccount` checks `AccountRepository.findByCpf`.
  - Throws `DuplicateCpfException` when necessary.
  - Controller/exception handler is responsible for mapping that exception to `409 Conflict`.

### 6. Testing strategy

- **Unit tests**:
  - For **mappers**: ensure `AccountRequest` and `Account`/`AccountResponse` mapping is correct (and that collections are mapped properly).
  - For **service**:
    - Save account with non‑existing CPF.
    - Throw when CPF is already registered.
    - Fetch all accounts.
    - Fetch by CPF (found and not found).
- **Integration tests (optional / environment-dependent)**:
  - Start full Spring Boot context with embedded Tomcat and H2.
  - Use RestAssured to test HTTP behavior end‑to‑end (status codes and JSON payloads).

This separation helps catch regressions quickly and demonstrates how the system behaves both at the unit and at the API level.
