# ServiçoPro Backend — Épicos Jira

## Java 25 + Spring Boot 3.5.6 | Enterprise | DDD | Clean Architecture

---

## FASE 0 — Fundação do Projeto `[16 pts]`

> Pré-requisito de TODOS os épicos.

### F-1 — Setup do projeto Spring Boot `[5 pts]`

**Sub-tasks:**

- [ ] Criar projeto via Spring Initializr (Java 25, Spring Boot 3.5.6, packaging JAR)
- [ ] Definir estrutura de pacotes DDD por módulo:
      `com.servicepro.{auth,users,providers,catalog,requests,payments,reviews,notifications,admin,audit}.{domain,application,infrastructure,interfaces}`
- [ ] Configurar `pom.xml`: Lombok, MapStruct, Flyway, Spring Security, Resilience4j, Springdoc OpenAPI 3.1, Testcontainers, Argon2 (Bouncy Castle), jjwt
- [ ] `application.yml` com profiles `dev`, `test`, `prod` — segredos via env vars
- **AC:** `./mvnw spring-boot:run` sobe sem erros em perfil `dev`

### F-2 — Docker Compose local `[3 pts]`

**Sub-tasks:**

- [ ] `docker-compose.yml` com `postgres:17`, `redis:8-alpine`, `app` (health checks)
- [ ] `docker-compose.override.yml` para dev (volumes locais, portas expostas)
- [ ] `Dockerfile` multistage (builder JDK 25 → runtime JRE 25 slim)
- **AC:** `docker compose up` → todos os serviços saudáveis; app conecta ao PostgreSQL e Redis

### F-3 — Infraestrutura base de código `[5 pts]`

**Sub-tasks:**

- [ ] `GlobalExceptionHandler` com `NegocioExceptionHandler`, `ValidacaoExceptionHandler`, `GenericExceptionHandler`
- [ ] `ErrorResponse` record e `ApiResponse<T>` record genérico — formato: `{timestamp, status, error, message, path}`
- [ ] `SecurityFilterChain` base (tudo bloqueado; liberar `/api/v1/auth/**`, `/actuator/health`, `/swagger-ui/**`)
- [ ] Springdoc OpenAPI 3.1 com `SecurityScheme` Bearer JWT em `/swagger-ui.html`
- [ ] Flyway com `V0__baseline.sql`
- [ ] `BaseEntity` com `id (UUID)`, `createdAt`, `updatedAt`, `@PrePersist/@PreUpdate`
- **AC:** Handlers retornam JSON padronizado para 400, 401, 403, 404, 500

### F-4 — CI/CD base `[3 pts]`

**Sub-tasks:**

- [ ] `.github/workflows/ci.yml` → jobs: lint → test (Testcontainers) → build → docker-push (somente `main`)
- [ ] Cache Maven no CI
- [ ] `.env.example` com todas as variáveis necessárias
- **AC:** Pipeline verde no push para `main`

**DoD Fase 0:** Compila, testes ≥80%, Docker Compose funciona, CI verde, Swagger acessível.

---

## ÉPICO A — Identidade e Acesso `[55 pts]`

**Módulo:** `com.servicepro.auth.*`

### A-1 — Signup de usuário `[8 pts]`

**AC técnicos:**

- `POST /api/v1/auth/signup` recebe `SignupRequest` record: `{name, email, phone, password, role: CLIENT|PROVIDER}`
- Senha hasheada com **Argon2id** (`Argon2PasswordEncoder`)
- Email único validado no banco (`UNIQUE CONSTRAINT`) + `EmailAlreadyExistsException`
- Telefone validado com regex E.164 via `@Pattern`
- Retorna `201 Created` com `UserResponse` record (sem password_hash)

**Sub-tasks:**

- [ ] Entidade `User` (domain) + enum `Role {CLIENT, PROVIDER, ADMIN, SUPPORT, FINANCE}`
- [ ] `UserRepository` (JPA)
- [ ] `SignupRequest` record com `@Valid`
- [ ] `AuthService.signup()` com `@Transactional`
- [ ] `AuthController` com `@Slf4j`, `@RequiredArgsConstructor`
- [ ] Migration `V1__create_users_table.sql`
- [ ] Testes: `AuthServiceTest` (Mockito) + `AuthControllerTest` (MockMvc) — email duplicado, telefone inválido, sucesso

### A-2 — Login + JWT + Refresh Token `[13 pts]`

**AC técnicos:**

- `POST /api/v1/auth/login` → valida credenciais, gera **access token JWT** (15min, HS512) no body
- Gera **refresh token** (UUID v4, hash SHA-256 armazenado no banco + Redis TTL 7d)
- Refresh token: `Set-Cookie: refresh_token=...; HttpOnly; Secure; SameSite=Strict; Path=/api/v1/auth/refresh; Max-Age=604800`
- Redis key: `refresh_token:{userId}:{jti}` → TTL 7d

**Sub-tasks:**

- [ ] `JwtService` (infrastructure): `generateAccessToken()`, `generateRefreshToken()`, `validateToken()`, `extractClaims()`
- [ ] Entidade `RefreshToken` + `RefreshTokenRepository`
- [ ] `RefreshTokenService` com `@Transactional` em `create()` e `revoke()`
- [ ] `TokenPair` record `{accessToken, expiresIn}`
- [ ] `CookieUtils` helper para montar o `HttpOnly` cookie
- [ ] `CustomUserDetailsService` implementando `UserDetailsService`
- [ ] Migration `V2__create_refresh_tokens_table.sql` — `(id, user_id FK, token_hash, expires_at, revoked, created_at)`
- [ ] Testes: `JwtServiceTest`, `AuthServiceTest.login()`, `AuthControllerTest.login()`

### A-3 — Token Rotation `[8 pts]`

**AC técnicos:**

- `POST /api/v1/auth/refresh` — lê cookie `refresh_token` (**nunca no body**)
- Valida hash no banco + Redis; se revogado → `401 TokenRevokedException`
- Invalida token anterior (`revoked=true`, remove do Redis)
- Emite novo access token + novo refresh token (**rotation completa**)
- Mesmo refresh token usado 2x → **revogar TODOS os tokens do usuário** (detecção de replay attack)

**Sub-tasks:**

- [ ] `AuthService.refresh()` com lógica de rotation + detecção de replay
- [ ] `TokenRevokedException` + handler em `NegocioExceptionHandler`
- [ ] Testes: rotation normal, replay attack (deve revogar todos), token expirado

### A-4 — Logout `[3 pts]`

**AC técnicos:**

- `POST /api/v1/auth/logout` (autenticado) — revoga refresh token do cookie + limpa Redis
- Retorna cookie com `Max-Age=0` para forçar remoção no client
- Retorna `204 No Content`

**Sub-tasks:**

- [ ] `AuthService.logout()`
- [ ] Testes MockMvc com cookie válido e inválido

### A-5 — RBAC com Spring Security `[8 pts]`

**AC técnicos:**

- `JwtAuthenticationFilter` extrai Bearer token, valida, popula `SecurityContextHolder`
- Roles mapeadas para `GrantedAuthority`
- `@PreAuthorize("hasRole('ADMIN')")` nos controllers sensíveis
- `GET /api/v1/auth/me` → retorna perfil do usuário autenticado

**Sub-tasks:**

- [ ] `JwtAuthenticationFilter extends OncePerRequestFilter`
- [ ] `SecurityFilterChain` com regras por role
- [ ] `GET /api/v1/auth/me`
- [ ] Testes: `SecurityConfigTest` com MockMvc e roles diferentes

### A-6 — Rate Limiting (Brute Force Protection) `[5 pts]`

**AC técnicos:**

- Resilience4j `RateLimiter` em `/auth/login` e `/auth/signup`: máx 10 req/min por IP
- Ao exceder: `429 Too Many Requests` com header `Retry-After`
- Contador no Redis: `rate_limit:login:{ip}` com TTL 60s

**Sub-tasks:**

- [ ] `RateLimiterConfig` via `application.yml`
- [ ] `RateLimitFilter` ou `@RateLimiter` via AOP
- [ ] `RateLimitExceededException` + handler
- [ ] Teste: 11 requests consecutivos → 429 no 11º

### A-7 — Testes de Integração Auth `[5 pts]`

- [ ] `AuthIntegrationTest` com Testcontainers (PostgreSQL + Redis)
- [ ] Fluxo completo: signup → login → refresh → logout
- [ ] Replay attack
- [ ] Cobertura ≥ 80%

**DoD Épico A:** Endpoints funcionando, rate limiting operacional, token rotation validado, RBAC configurado, cobertura ≥80%.
**Dependências:** Fase 0

---

## ÉPICO B — Cadastro e Aprovação de Prestadores `[34 pts]`

**Módulo:** `com.servicepro.providers.*`

### B-1 — Perfil do prestador `[8 pts]`

**AC técnicos:**

- `POST /api/v1/providers/profile` (role PROVIDER) → cria `ProviderProfile` com status `PENDING_APPROVAL`
- Campos: `bio, cpf (validado por dígitos, armazenado como hash), address, profilePhotoUrl`
- `GET /api/v1/providers/{id}/profile` → público, retorna apenas perfis `APPROVED`

**Sub-tasks:**

- [ ] Entidade `ProviderProfile` + enum `ProviderStatus {PENDING_APPROVAL, APPROVED, REJECTED, SUSPENDED}`
- [ ] `ProviderProfileRequest/Response` records + MapStruct mapper
- [ ] Migration `V3__create_provider_profiles_table.sql`
- [ ] `ProviderService.createProfile()` com `@Transactional`
- [ ] Testes: MockMvc + ServiceTest

### B-2 — Workflow de aprovação `[8 pts]`

**AC técnicos:**

- `PATCH /api/v1/admin/providers/{id}/approve` (ADMIN|SUPPORT) → status `APPROVED`, dispara evento
- `PATCH /api/v1/admin/providers/{id}/reject` (ADMIN|SUPPORT) → requer `reason`, status `REJECTED`
- `GET /api/v1/admin/providers?status=PENDING_APPROVAL` → listagem paginada
- Transição inválida (ex: APPROVED → APPROVED) → `InvalidStatusTransitionException`

**Sub-tasks:**

- [ ] `ProviderApprovalService` com transições de estado
- [ ] `ProviderApprovalEvent` via `ApplicationEventPublisher`
- [ ] Testes: transições válidas e inválidas

### B-3 — Documentos e verificação `[5 pts]`

**AC técnicos:**

- `POST /api/v1/providers/{id}/documents` → upload via URL (storage externo)
- Máx 5 documentos por prestador

**Sub-tasks:**

- [ ] Entidade `ProviderDocument`
- [ ] Migration `V4__create_provider_documents_table.sql`
- [ ] Testes

### B-4 — Especialidades e habilidades `[5 pts]`

**AC técnicos:**

- `PUT /api/v1/providers/{id}/specialties` → define lista de `categoryId[]`

**Sub-tasks:**

- [ ] Migration `V5__create_provider_specialties_table.sql`
- [ ] Testes

### B-5 — Testes de integração `[3 pts]`

- [ ] Fluxo onboarding completo com Testcontainers

**DoD Épico B:** Onboarding funcional, aprovação/rejeição operacional, cobertura ≥80%.
**Dependências:** Fase 0, Épico A

---

## ÉPICO C — Catálogo e Descoberta `[21 pts]`

**Módulo:** `com.servicepro.catalog.*`

### C-1 — Categorias de serviços `[5 pts]`

**AC técnicos:**

- Migration `V6__create_categories_table.sql` — `(id UUID, name, slug UNIQUE, icon, color, description, active)`
- `GET /api/v1/catalog/categories` → público, cacheado Redis TTL 10min (`@Cacheable`)
- `POST /api/v1/admin/catalog/categories` (ADMIN) → invalida cache

**Sub-tasks:**

- [ ] Entidade `Category`, migration `V6`, `CategoryService`, `CategoryController`
- [ ] `RedisCacheManager` via `CacheConfig`
- [ ] Testes: MockMvc + invalidação de cache

### C-2 — Serviços (offerings) `[5 pts]`

**AC técnicos:**

- Migration `V7__create_services_table.sql` — `(id UUID, provider_id FK, category_id FK, name, description, price_cents, duration_minutes, active)`
- `GET /api/v1/providers/{id}/services` → público
- `POST /api/v1/providers/{id}/services` (PROVIDER, dono)
- `PUT /api/v1/providers/{id}/services/{serviceId}`

### C-3 — Busca e Descoberta `[8 pts]`

**AC técnicos:**

- `GET /api/v1/catalog/professionals?categoryId=&lat=&lng=&radiusKm=&minRating=&page=&size=`
- Ordenação: distância / avaliação / preço
- Paginação com `Page<ProviderSearchResponse>`
- Query Haversine via SQL nativo
- Cache Redis por hash de parâmetros (TTL 5min)

**Sub-tasks:**

- [ ] `ProviderSearchQuery` record
- [ ] `CatalogQueryService` com `@Transactional(readOnly=true)`
- [ ] Migration `V8__add_provider_location.sql`
- [ ] Testes: MockMvc com filtros variados

### C-4 — Testes de integração Catalog `[3 pts]`

**DoD Épico C:** Busca funcional com filtros, cache Redis operacional, cobertura ≥80%.
**Dependências:** Fase 0, Épico A, Épico B

---

## ÉPICO D — Solicitações e Execução (FSM) `[55 pts]`

**Módulo:** `com.servicepro.requests.*`

### D-1 — Criação de solicitação `[8 pts]`

**AC técnicos:**

- `POST /api/v1/requests` (CLIENT) → cria com status `DRAFT`
- `POST /api/v1/requests/{id}/submit` → transição `DRAFT → OPEN`
- Migration `V9__create_service_requests_table.sql`
- Migration `V10__create_service_request_events_table.sql` — **imutável, sem UPDATE/DELETE**

**Sub-tasks:**

- [ ] Enum `RequestStatus {DRAFT, OPEN, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED, DISPUTED}`
- [ ] Entidade `ServiceRequest` + entidade `ServiceRequestEvent` (sem setter de `createdAt`)
- [ ] `RequestFSM` (domain service) com mapa de transições
- [ ] Migrations `V9` e `V10`
- [ ] Testes: `RequestFSMTest` cobrindo todas as transições

### D-2 — FSM completa — todas as transições `[13 pts]`

**Máquina de estados:**

```
DRAFT       → OPEN        | ator: CLIENT
OPEN        → ACCEPTED    | ator: PROVIDER
OPEN        → CANCELLED   | ator: CLIENT | ADMIN
ACCEPTED    → IN_PROGRESS | ator: PROVIDER
ACCEPTED    → CANCELLED   | ator: CLIENT | PROVIDER | ADMIN (taxa aplicável)
IN_PROGRESS → COMPLETED   | ator: PROVIDER (requer confirmação CLIENT)
IN_PROGRESS → DISPUTED    | ator: CLIENT | PROVIDER
COMPLETED   → DISPUTED    | ator: CLIENT (prazo: 24h)
DISPUTED    → COMPLETED   | ator: ADMIN | SUPPORT
DISPUTED    → CANCELLED   | ator: ADMIN | SUPPORT
```

- Qualquer outra transição → `InvalidStateTransitionException(from, to)`
- Cada transição registra evento imutável em `service_request_events`
- `COMPLETED` → publica `RequestCompletedEvent` (dispara pagamento)
- `CANCELLED` → publica `RequestCancelledEvent` (notificação + estorno)

**Sub-tasks:**

- [ ] `RequestFSM` com `TransitionRule` record `{from, to, allowedActors}` e mapa imutável estático
- [ ] `RequestTransitionService` (application) — orquestra FSM + persiste evento + publica `ApplicationEvent`
- [ ] `POST /api/v1/requests/{id}/accept` (PROVIDER)
- [ ] `POST /api/v1/requests/{id}/start` (PROVIDER)
- [ ] `POST /api/v1/requests/{id}/complete` (PROVIDER)
- [ ] `POST /api/v1/requests/{id}/cancel` (CLIENT|PROVIDER|ADMIN)
- [ ] `POST /api/v1/requests/{id}/dispute` (CLIENT|PROVIDER)
- [ ] `POST /api/v1/admin/requests/{id}/resolve-dispute` (ADMIN|SUPPORT)
- [ ] Testes: cada transição válida + cada transição inválida

### D-3 — Timeline de eventos `[8 pts]`

**AC técnicos:**

- `GET /api/v1/requests/{id}/events` → retorna `List<RequestEventResponse>` ordenado por `created_at ASC`
- Sem endpoints PUT/DELETE para eventos (imutabilidade garantida na camada de serviço e banco)
- Acesso: dono CLIENT | PROVIDER assignado | ADMIN

### D-4 — Listagem e filtragem `[5 pts]`

**AC técnicos:**

- `GET /api/v1/requests?status=&page=&size=` (CLIENT vê os próprios; PROVIDER vê os aceitos)
- `GET /api/v1/admin/requests?status=&clientId=&providerId=` (ADMIN)

### D-5 — Testes de integração FSM `[8 pts]`

- [ ] `ServiceRequestIntegrationTest` com Testcontainers
- [ ] Fluxo feliz: DRAFT→OPEN→ACCEPTED→IN_PROGRESS→COMPLETED
- [ ] Disputas, cancelamentos e transições inválidas
- [ ] Cobertura ≥80%

**DoD Épico D:** Todas as transições da FSM implementadas e testadas, nenhuma transição inválida possível via API, timeline imutável, cobertura ≥80%.
**Dependências:** Fase 0, Épico A, Épico B, Épico C

---

## ÉPICO E — Pagamentos e Repasses `[34 pts]`

**Módulo:** `com.servicepro.payments.*`

### E-1 — Integração com PSP `[13 pts]`

**AC técnicos:**

- `POST /api/v1/payments` (CLIENT) → inicia pagamento para `requestId`
- PSP encapsulado com porta `PspGatewayPort` (domain) + adapter concreto (infrastructure)
- Resilience4j `CircuitBreaker` no PSP → fallback: enfileira no Redis para retry
- Migration `V11__create_payments_table.sql`

**Sub-tasks:**

- [ ] Interface `PspGatewayPort` com `createPaymentIntent()` e `refund()`
- [ ] Adapter concreto (Pagar.me / Stripe / MercadoPago — escolher)
- [ ] Entidade `Payment` + enum `PaymentStatus {PENDING, PROCESSING, PAID, FAILED, REFUNDED}`
- [ ] `PaymentService.initiate()` com `@Transactional`
- [ ] `CircuitBreakerConfig` para o PSP
- [ ] Testes com mock do adapter

### E-2 — Webhook do PSP `[8 pts]`

**AC técnicos:**

- `POST /api/v1/payments/webhook` → público, validado por assinatura **HMAC** no header
- Eventos: `payment.succeeded`, `payment.failed`, `refund.created`
- Em `payment.succeeded` → `PAID`, publica `PaymentPaidEvent`
- Idempotência: verificar `psp_transaction_id` antes de processar

**Sub-tasks:**

- [ ] `WebhookSignatureValidator` (HMAC)
- [ ] `PaymentWebhookService` com `@Transactional`
- [ ] Testes: payload válido, assinatura inválida (→400)

### E-3 — Split e repasse `[8 pts]`

**AC técnicos:**

- Taxa da plataforma configurável por categoria (default 15%)
- Repasse agendado para D+2 após `PAID`
- `GET /api/v1/finance/payouts?providerId=&status=` (FINANCE|ADMIN)
- Migration `V12__create_payouts_table.sql`

**Sub-tasks:**

- [ ] `FeeCalculationService` (domain service)
- [ ] `PayoutService.schedule()` com `@Transactional`
- [ ] `@Scheduled` job para processar payouts agendados
- [ ] Testes: cálculo de taxa, job de repasse

### E-4 — Testes de integração `[5 pts]`

**DoD Épico E:** Fluxo completo intent→webhook→repasse, circuit breaker operacional, cobertura ≥80%.
**Dependências:** Fase 0, Épico A, Épico D (request COMPLETED)

---

## ÉPICO F — Avaliações e Reputação `[21 pts]`

**Módulo:** `com.servicepro.reviews.*`

### F-1 — Submissão de avaliação `[8 pts]`

**AC técnicos:**

- `POST /api/v1/reviews` (CLIENT ou PROVIDER) → avalia a contraparte após `COMPLETED`
- Migration `V13__create_reviews_table.sql` — `UNIQUE(request_id, reviewer_id)`
- Regras: só após `COMPLETED`, 1 review por par, prazo de 7 dias após completion
- `rating` 1–5 obrigatório, `comment` opcional máx 500 chars

**Sub-tasks:**

- [ ] Entidade `Review` + migration `V13`
- [ ] `ReviewService.submit()` com validações de negócio
- [ ] `SubmitReviewRequest` record com `@Valid`
- [ ] Testes: fora do prazo, duplicado, request não completada

### F-2 — Score de reputação `[8 pts]`

**AC técnicos:**

- Média ponderada das últimas 100 avaliações
- Desnormalizado em `provider_profiles`: `average_rating`, `total_reviews`
- Atualizado via `@EventListener(ReviewCreatedEvent.class)` com `@Async`
- `GET /api/v1/providers/{id}/rating` → `{averageRating, totalReviews, distribution}`

**Sub-tasks:**

- [ ] `ReviewScoreService.recalculate()` assíncrono
- [ ] Migration `V14__add_rating_to_providers.sql`
- [ ] Testes: cálculo de score e distribuição

### F-3 — Listagem `[5 pts]`

- `GET /api/v1/providers/{id}/reviews?page=&size=` → público, paginado
- `GET /api/v1/reviews/received` → avaliações recebidas pelo usuário autenticado

**DoD Épico F:** Avaliações submetidas, score desnormalizado e calculado, cobertura ≥80%.
**Dependências:** Fase 0, Épico A, Épico D (COMPLETED)

---

## ÉPICO G — Web Admin Operacional `[21 pts]`

**Módulo:** `com.servicepro.admin.*`

### G-1 — Dashboard de métricas `[5 pts]`

- `GET /api/v1/admin/dashboard` (ADMIN|SUPPORT) — totais: usuários, prestadores pendentes, solicitações ativas, disputas, volume diário
- Cacheado Redis TTL 1min

### G-2 — Gestão de usuários `[8 pts]`

- `GET /api/v1/admin/users?role=&active=&page=`
- `PATCH /api/v1/admin/users/{id}/suspend` → suspende + revoga **todos** os refresh tokens
- `PATCH /api/v1/admin/users/{id}/activate`
- `GET /api/v1/admin/users/{id}` → perfil completo

### G-3 — Gestão financeira `[5 pts]`

- `GET /api/v1/admin/finance/transactions?from=&to=`
- `POST /api/v1/admin/finance/payouts/{id}/force` (FINANCE) → repasse manual
- `GET /api/v1/admin/finance/summary`

### G-4 — Auditoria de ações admin `[3 pts]`

- Toda ação admin logada em `audit_logs (id UUID, actor_id, action, entity_type, entity_id, metadata JSONB, ip_address, created_at)`
- Migration `V15__create_audit_logs_table.sql`
- Implementado via AOP `@Around` + anotação `@AuditAction`

**DoD Épico G:** Dashboard funcional, moderação operacional, auditoria imutável, cobertura ≥80%.
**Dependências:** Todos os épicos anteriores

---

## ÉPICO H — Observabilidade, Segurança e Compliance `[21 pts]`

### H-1 — Actuator + Prometheus + Grafana `[5 pts]`

- Actuator na porta 8081 para scrape Prometheus
- Métricas customizadas: `requests.created.total`, `payments.processed.total`, `auth.login.failed.total`
- Health checks: PostgreSQL, Redis, PSP
- `docker-compose.yml` com `prometheus` + `grafana` pré-configurados

### H-2 — Distributed Tracing `[3 pts]`

- Micrometer Tracing + Zipkin (ou OTLP)
- `traceId` propagado em todos os logs via MDC
- Logs estruturados em JSON (`logstash-logback-encoder`)

### H-3 — Segurança Hardened `[8 pts]`

- Headers: `Content-Security-Policy`, `X-Frame-Options`, `X-Content-Type-Options`, `HSTS`
- CORS explícito para origens do app mobile
- Segredos 100% via env vars (placeholder Vault para prod)
- OWASP Dependency Check no CI
- `PATCH /api/v1/auth/change-password` → requer senha atual, invalida todos os tokens

### H-4 — LGPD — Direito ao esquecimento `[5 pts]`

- `DELETE /api/v1/users/me` → anonimiza dados: `name="Usuário Removido"`, `email=uuid@deleted`, `phone=null`, `active=false`
- Dados financeiros mantidos (obrigação fiscal)
- Registrado em `audit_logs`
- `GET /api/v1/users/me/data-export` → portabilidade em JSON

**DoD Épico H:** Métricas no Prometheus, tracing funcional, headers de segurança presentes, anonimização validada, cobertura ≥80%.
**Dependências:** Fase 0, Épico A

---

## Grafo de Dependências

```
Fase 0 (Fundação)
    └── Épico A (Auth) ─────────────────────────────────┐
            ├── Épico B (Providers)                      │
            │       └── Épico C (Catálogo)               │
            │               └── Épico D (FSM Requests)   │
            │                       ├── Épico E (Payments)│
            │                       └── Épico F (Reviews) │
            │                               └── Épico G (Admin)
            └── Épico H (Segurança/Obs) ─────────────────┘
```

---

## Resumo de Story Points

| Épico     | Nome                                    | Story Points |
| --------- | --------------------------------------- | ------------ |
| Fase 0    | Fundação                                | 16           |
| A         | Identidade e Acesso                     | 55           |
| B         | Cadastro e Aprovação de Prestadores     | 34           |
| C         | Catálogo e Descoberta                   | 21           |
| D         | Solicitações e Execução (FSM)           | 55           |
| E         | Pagamentos e Repasses                   | 34           |
| F         | Avaliações e Reputação                  | 21           |
| G         | Web Admin Operacional                   | 21           |
| H         | Observabilidade, Segurança e Compliance | 21           |
| **Total** |                                         | **278 pts**  |

---

## Próximos passos

1. **Criar o projeto Spring Boot** (Fase 0 → Story F-1)
2. **Confirmar o PSP** — Pagar.me, Stripe ou Mercado Pago?
3. **Definir se quer Épico I** para notificações (FCM push + email) como épico independente
4. **Definir storage de arquivos** — S3/GCS com pre-signed URLs (documentos de prestadores)
