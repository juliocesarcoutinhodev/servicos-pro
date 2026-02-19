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

## CORS

- Configurado por variáveis de ambiente (`APP_CORS_*`).
- Em `dev`, defaults permitem `http://localhost:5173`, `http://localhost:3000` e `http://localhost:19006`.
- Em `staging/prod`, ajuste `APP_CORS_ALLOWED_ORIGINS` para os domínios reais do frontend web.
- Para mobile React Native nativo, CORS normalmente não se aplica, mas para web/admin no browser se aplica.

## Testes

- `./mvnw test`

## Documentação de API

- Collections Postman oficiais:
  - `docs/postman/servicepro-backend.local.postman_collection.json`
  - `docs/postman/servicepro-backend.prod.postman_collection.json`
- Variáveis da collection:
  - `baseUrl` para endpoints da API
  - `managementBaseUrl` para endpoints Actuator
- Regra: todo novo endpoint deve ser adicionado/atualizado nessa collection.

## Deploy em VPS Ubuntu (GitHub Actions)

O pipeline em `.github/workflows/backend-ci.yml` faz:

1. lint
2. test
3. build
4. push da imagem no GHCR (`main`)
5. deploy via SSH na VPS (`main`)

### Arquivos usados no deploy

- `docker-compose.prod.yml`: stack de produção (`app`, `postgres`, `redis`)
- `.env.production` remoto: variáveis sensíveis e configuração do ambiente de produção

### Secrets obrigatórios no GitHub

- `VPS_HOST`: IP público da VPS
- `VPS_USER`: usuário SSH (ex.: `root` ou `ubuntu`)
- `VPS_SSH_KEY`: chave privada SSH (formato OpenSSH)
- `VPS_SSH_PORT`: porta SSH (geralmente `22`)
- `VPS_APP_DIR`: pasta de deploy no servidor (ex.: `/opt/servicepro/backend`)
- `GHCR_USERNAME`: usuário com acesso ao pacote no GHCR
- `GHCR_TOKEN`: token com permissão de leitura de pacotes (`read:packages`)
- `VPS_ENV_PRODUCTION`: conteúdo completo do `.env.production` (multi-line)
- `VPS_ENV_PRODUCTION_B64`: alternativa em Base64 para o `.env.production`

### Bootstrap inicial da VPS (uma vez)

1. Instalar Docker + Docker Compose plugin
2. Criar pasta de deploy (mesmo valor de `VPS_APP_DIR`)
3. Liberar portas no firewall (mínimo `22` e `8080`)
4. (Opcional e recomendado) colocar Nginx/Caddy na frente da API com HTTPS
