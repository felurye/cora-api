CREATE TABLE account
(
    id   serial PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL
);