## Adicionar Saldo e Status Ativo

### Contexto
O candidato já tem uma entidade Account básica com `name` e `cpf`. Vamos adicionar campos para controle de saldo e status da conta.

### Regras de Negócio
- Adicionar campo `balance` (Double) à entidade Account
- Adicionar campo `active` (Boolean) à entidade Account
- Todas as contas criadas devem ter `active = true` por padrão
- Saldo inicial deve ser R$ 0,00 (será ajustado no Objetivo 2 com cupons)

### Critérios de Aceitação
✅ Account possui campos `balance` e `active`
✅ POST /accounts retorna conta com `balance: 0.0` e `active: true`
✅ Banco de dados persiste corretamente os novos campos

### Exemplo de Response
```json
POST /accounts
{
    "name": "João Silva",
    "cpf": "12345678901"
}

Response 201 Created:
{
    "id": 1,
    "name": "João Silva",
    "cpf": "12345678901",
    "balance": 0.0,
    "active": true
}
```
