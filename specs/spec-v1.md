# Desafio Técnico Fullstack

## Stack

**Backend (Obrigatório):**
- **Linguagem**: Kotlin ou Java
- **Framework**: Spring Boot 3.x
- **Banco de Dados**: H2 (em memória) ou banco relacional à sua escolha
- **Java Version**: 17+

**Frontend (fornecido durante a entrevista):**
- **React** com **TypeScript** será enviado no dia
- Você **não precisa** desenvolver ou testar o frontend antes da entrevista
- Durante a entrevista, trabalharemos juntos na integração
- **Pré-requisito**: Node.js versão 20+ instalado

## O Desafio

Desenvolva uma aplicação básica para listar e criar contas bancárias.

### Requisitos Funcionais

#### Backend (API REST)

**1. Criar conta**
- **Endpoint**: `POST /accounts`
- **Request**: `{ "name": "Nome do Usuário", "cpf": "12345678901" }`
- **Response**: 201 Created

**2. Listar contas**
- **Endpoint**: `GET /accounts`
- **Response**: 200 OK

### O que NÃO precisa fazer

- Editar/deletar contas
- Validações e tratamento de erros elaborados
- Testes (mas são bem-vindos)
- Autenticação

## Checklist de Preparação

**Antes da entrevista, você deve ter:**

1. Backend rodando localmente
2. Dois endpoints funcionando (POST /accounts e GET /accounts)
3. CORS configurado (para aceitar requisições do frontend)
4. Banco de dados funcional (H2 ou outro)
5. Node.js 20+ instalado

**Durante a entrevista:**
- Apresentação da sua solução (5-10 min)
- Receberá o projeto frontend React/TypeScript
- Pair programming: integração frontend + backend + melhorias
- Discussão sobre decisões técnicas

## Dicas

**Para o Backend:**
- H2 em memória já vem configurado no Spring Boot
- Configure CORS: adicione `@CrossOrigin` no controller ou configure globalmente
- Teste os endpoints com Postman/Insomnia/curl

**Para a Entrevista:**
- O projeto frontend virá pré-estruturado com componentes base
- Você só precisará integrar com sua API
- Se tiver dúvidas durante a entrevista, pergunte! É pair programming :)

## Banco de Dados

Se usar H2 em memória:
- **Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (deixe em branco)

**Lembre-se**: Este exercício é para preparação. A avaliação real acontece durante a entrevista através do pair programming!
