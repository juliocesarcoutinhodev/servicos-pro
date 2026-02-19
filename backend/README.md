# ServicePro Backend

Backend do ServicePro com Java 25 + Spring Boot 3.5.6, arquitetura DDD (modular monolith) e foco em padrão enterprise.

## Stack principal

- Java 25
- Spring Boot 3.5.6
- PostgreSQL
- Redis
- Flyway
- Spring Security
- MapStruct
- Docker / Docker Compose

## Estrutura

- `src/main/java/com/servicepro/*`: módulos por domínio (`auth`, `users`, `providers`, etc.)
- `src/main/resources/db/migration`: migrations Flyway
- `docs/postman`: collection oficial da API

## Convenções

- DDD estrito: regras de negócio no domínio
- Controllers limpos: apenas orquestração HTTP
- Mapeamentos com MapStruct
- Tabelas e constraints com prefixo `tb_` / `uk_tb_*`

## Ambiente local (IDE + serviços via Docker)

1. Configure variáveis usando `.env.local`
2. Suba somente infraestrutura:
   - `docker compose -f docker-compose.local.yml up -d`
3. Rode a aplicação na IDE com profile `dev`
4. Portas padrão:
   - API: `http://localhost:8080`
   - Actuator/management: `http://localhost:8081`

## Testes

- `./mvnw test`

## Documentação de API

- Collection Postman oficial: `docs/postman/servicepro-backend.postman_collection.json`
- Variáveis da collection:
  - `baseUrl` para endpoints da API
  - `managementBaseUrl` para endpoints Actuator
- Regra: todo novo endpoint deve ser adicionado/atualizado nessa collection.
