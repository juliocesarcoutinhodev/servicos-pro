# Docs

Documentação funcional/técnica do backend.

## Padrão da API

- Fonte oficial para testes manuais: `docs/postman/servicepro-backend.postman_collection.json`
- Sempre que criar ou alterar endpoint:
  - atualizar request/response na collection
  - manter nomes curtos de negócio (ex: `Signup Client`, `Login`)
  - descrever método/path no campo `description` da request
  - incluir exemplos de payload mínimos válidos
  - preferir variáveis de collection (`{{...}}`) em vez de valores fixos
  - manter scripts de `test` para salvar tokens/valores dinâmicos quando aplicável
  - para endpoints com rate limit, mapear `X-RateLimit-Remaining`, `X-RateLimit-Reset` e `Retry-After` na collection
