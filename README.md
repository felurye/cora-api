# Minibank API

A small banking backend used as a technical exercise.
The goal is to expose a simple REST API to **create** and **list** bank accounts, with a **coupon system** for initial balance bonuses, using **Spring Boot**, **Java 21**, and an **H2 in-memory database**.

## Stack & Requirements

- **Language**: Java 21+
- **Framework**: Spring Boot
- **Build Tool**: Maven
- **Database**: H2 (in-memory)

Default HTTP port: **`8080`**

## Running the Application

### 1. Prerequisites

- **Java**: 21+
- **Maven**: 3.8+

### 2. Clone the Repository

```bash
git clone https://github.com/felurye/mini-bank.git
cd mini-bank
```

### 3. Run with Maven

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

On startup, the application seeds the coupon `CORA10` automatically if it does not exist in the database.

### 4. H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

Tables available for inspection: `ACCOUNT`, `COUPON`.

## Building & Running Tests

```bash
mvn test
```

Test classes:

| Class | Scope |
|---|---|
| `AccountMapperTest` | Unit - DTO mapping |
| `AccountServiceTest` | Unit - business rules |
| `CouponTest` | Unit - coupon entity methods |
| `GlobalExceptionHandlerTest` | Unit - HTTP error responses |

## API

### POST /accounts - Create account

**Request body:**

```json
{
  "name": "Maria Silva",
  "cpf": "98765432100",
  "referralCode": "CORA10"
}
```

- `name`: required
- `cpf`: required, must be unique
- `referralCode`: optional - when provided, applies the coupon bonus to the initial balance

**Success response - `201 Created`:**

```json
{
  "id": 1,
  "name": "Maria Silva",
  "cpf": "98765432100",
  "balance": 10.0,
  "active": true
}
```

**Error responses:**

| Situation | Status | Description |
|---|---|---|
| Missing/blank fields | `400 Bad Request` | Validation failed |
| CPF already registered | `409 Conflict` | Duplicate CPF |
| Coupon not found | `422 Unprocessable Entity` | Invalid referral code |
| Coupon limit reached | `422 Unprocessable Entity` | Coupon exhausted |
| Concurrent coupon conflict | `409 Conflict` | Race condition on coupon update - retry the request |

**Error response body:**

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Coupon 'CORA10' has reached its usage limit.",
  "path": "/accounts",
  "timestamp": "2026-04-08T12:00:00Z"
}
```

---

### GET /accounts - List accounts

**Success response - `200 OK`:**

```json
[
  {
    "id": 1,
    "name": "Maria Silva",
    "cpf": "98765432100",
    "balance": 10.0,
    "active": true
  }
]
```

---

### Coupon: CORA10

The coupon `CORA10` is seeded on startup with a bonus of **R$ 10.00** and a usage limit of **2 uses**.
Accounts created without a coupon start with `balance: 0.0`.

## Architecture

The project follows a layered architecture with clear separation between API, domain, and infrastructure concerns.

### Package structure

- `com.minibank`
  - `MinibankApplication` - Spring Boot entry point.
- `com.minibank.api`
  - `controller` - REST controllers (`AccountController`).
  - `request` - Input DTOs (`AccountRequest`).
  - `response` - Output DTOs (`AccountResponse`, `ErrorResponse`).
  - `mapper` - DTO-to-entity mapping (`AccountMapper`).
  - `exception` - Business exceptions and global handler (`GlobalExceptionHandler`).
- `com.minibank.domain`
  - `entities` - JPA entities (`Account`, `Coupon`).
  - `repository` - Spring Data repositories (`AccountRepository`, `CouponRepository`).
  - `service` - Business logic (`AccountService`).
- `com.minibank.config`
  - `AppConfig` - `ModelMapper` bean and global CORS configuration.
  - `DataLoader` - Seeds initial data on startup.

## Technical Decisions

### 1. H2 in-memory database

- **Why**: Zero-setup, no external dependency - runs with a single `mvn spring-boot:run`.
- **Trade-off**: Data is lost on restart. PostgreSQL config is already present in `application.properties`, commented out, for when a persistent database is needed.

### 2. Layered architecture (Controller → Service → Repository)

- Controllers are thin: only HTTP concerns (status codes, DTOs).
- Business rules live in the service layer, independent of HTTP.
- Each layer is testable in isolation.

### 3. DTOs and ModelMapper

- JPA entities are never exposed directly as API contracts.
- `AccountMapper` centralizes all mapping logic.
- ModelMapper reduces boilerplate for property-to-property mappings.

### 4. Coupon system

- Coupons are database entities (`Coupon`) with a code, usage limit, usage counter, and bonus amount.
- The service validates existence and availability before applying a coupon.
- `CORA10` is seeded by `DataLoader` on every startup.

### 5. Transactional account creation with optimistic locking

- `AccountService.saveAccount` is annotated with `@Transactional`: if any step fails, the entire operation rolls back - including the coupon usage counter increment.
- The `Coupon` entity uses `@Version` for optimistic locking. Concurrent requests for the same coupon are detected by Hibernate and rejected with a `409 Conflict`, preventing double-counting.

### 6. Global CORS configuration

- Configured via `WebMvcConfigurer` in `AppConfig`, applying to all endpoints.
- Currently permissive (`allowedOrigins("*")`) for development purposes.
- For production, replace with the specific frontend origin. Note: `allowedOrigins("*")` is incompatible with `allowCredentials(true)`.

### 7. Global exception handler

- `GlobalExceptionHandler` (`@RestControllerAdvice`) maps all business exceptions to structured HTTP responses.
- All error responses share the same `ErrorResponse` shape: `status`, `error`, `message`, `path`, `timestamp`.
- Validation errors additionally include a list of field-level details.
