## Sistema de Cupons de Indicação

### Contexto
Implementar sistema de cupons que concede saldo inicial para novos clientes através de código de indicação.

### Regras de Negócio
- Adicionar parâmetro **opcional** `referralCode` ao request de criação de conta
- Cupom válido: **`CORA10`** → concede saldo inicial de **R$ 10,00**
- Se cupom válido fornecido → `balance = 10.0`
- Se nenhum cupom fornecido → `balance = 0.0`
- Se cupom **inválido** fornecido → retornar erro **400 Bad Request**

### Critérios de Aceitação
✅ Request aceita parâmetro opcional `referralCode`
✅ Cupom CORA10 concede R$ 10,00 de saldo inicial
✅ Cupom inválido retorna erro 400 com mensagem clara
✅ Sem cupom = saldo R$ 0,00

### Exemplos de API

**Com cupom válido:**
```json
POST /accounts
{
    "name": "Maria Silva",
    "cpf": "98765432100",
    "referralCode": "CORA10"
}

Response 201 Created:
{
    "id": 2,
    "name": "Maria Silva",
    "cpf": "98765432100",
    "balance": 10.0,
    "active": true
}
```

**Com cupom inválido:**
```json
POST /accounts
{
    "name": "Pedro Santos",
    "cpf": "11122233344",
    "referralCode": "INVALIDO"
}

Response 400 Bad Request:
{
    "error": "Código de indicação inválido"
}
```

---

### Observação - Evolução Arquitetural

Este spec descreve o comportamento original da feature. A implementação evoluiu nos seguintes pontos:

**Cupons baseados em banco de dados**
O código `CORA10` deixou de ser hardcoded no service. Os cupons são agora registros na entidade `Coupon`, com código, limite de usos, contador e valor de bônus. O cupom `CORA10` é inserido automaticamente via `DataLoader` na inicialização da aplicação.

**HTTP status para erros de cupom**
O spec original define `400 Bad Request` para cupom inválido. A implementação retorna `422 Unprocessable Entity`, que é semanticamente mais preciso: a request é sintaticamente válida, mas uma regra de negócio impediu o processamento. Dois cenários distintos foram mapeados:
- Cupom não encontrado → 422
- Limite de usos atingido → 422

**Formato da resposta de erro**
O spec mostra `{"error": "..."}`. A implementação retorna um `ErrorResponse` estruturado com `status`, `error`, `message`, `path` e `timestamp`, padrão adotado em todos os erros da API.

Para o comportamento completo do sistema de cupons, incluindo controle de limite de usos e tratamento de concorrência, ver `limite-cupom-spec.md`.