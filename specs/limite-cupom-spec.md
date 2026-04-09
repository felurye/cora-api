# Controle de Limite de Cupons

## Contexto

Na criação de contas, o usuário pode informar um cupom de indicação para receber um bônus no saldo inicial.
Cada cupom tem um limite de usos - quando atingido, o cupom deixa de ser válido.

## Regras

- Cada cupom tem um código único, um limite de usos e um valor de bônus associado.
- Apenas um cupom pode ser utilizado por conta.
- Antes de criar a conta, o sistema deve verificar se o cupom ainda tem usos disponíveis.
- Se o cupom estiver com o limite atingido, a criação da conta deve ser rejeitada com erro.
- Se o código informado não corresponder a nenhum cupom cadastrado, a criação deve ser rejeitada com erro.
- A cada uso bem-sucedido, o contador de usos do cupom deve ser incrementado.
- A criação de conta sem cupom permanece permitida.

## Critérios de Aceitação

1. Dado um cupom com usos disponíveis, quando utilizado na criação de uma conta, então a conta deve ser criada com o bônus aplicado e o contador de usos do cupom deve ser incrementado.
2. Dado um cupom cujo limite de usos foi atingido, quando informado na criação de uma conta, então a conta não deve ser criada e deve ser retornado um erro indicando que o cupom não está mais disponível.
3. Dado um código de cupom inexistente, quando informado na criação de uma conta, então a conta não deve ser criada e deve ser retornado um erro indicando que o cupom não foi encontrado.
4. Dado que nenhum cupom foi informado, quando uma conta for criada, então a conta deve ser criada com saldo zero.

## Detalhes Técnicos

### Entidade `Coupon`

| Campo        | Tipo    | Descrição                                  |
|--------------|---------|--------------------------------------------|
| `id`         | Integer | Identificador único                        |
| `code`       | String  | Código do cupom (único)                    |
| `usageLimit` | Integer | Número máximo de usos permitidos           |
| `usageCount` | Integer | Contador de usos (inicializado em 0)       |
| `bonusAmount`| Double  | Valor creditado no saldo inicial da conta  |

### Alterações na entidade `Account`

- Remover o campo `referralCode: String`.
- Adicionar FK `coupon` (nullable) apontando para `Coupon`, para fins de auditoria.

### Fluxo de criação de conta com cupom

1. Receber o `referralCode` via `AccountRequest`.
2. Buscar o cupom pelo código - se não encontrado, rejeitar com erro.
3. Verificar se `usageCount < usageLimit` - se não, rejeitar com erro.
4. Incrementar `usageCount` e persistir o cupom.
5. Aplicar `bonusAmount` como saldo inicial da conta.
6. Vincular o cupom à conta via FK.

### Fluxo de criação de conta sem cupom

- `referralCode` não informado: saldo inicial = `0.0`, FK `coupon` permanece `null`.

### Exceções

| Situação                  | Exceção                       | HTTP  |
|---------------------------|-------------------------------|-------|
| Cupom não encontrado      | `CouponNotFoundException`     | 422   |
| Limite de usos atingido   | `CouponLimitReachedException` | 422   |
