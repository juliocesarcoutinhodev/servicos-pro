# ServiçoPro — Arquitetura de Software e Análise de Requisitos do Backend

> Objetivo: definir um **plano completo de backend** para o app de prestação de serviços, mantendo o frontend atual (mobile para cliente/prestador) e considerando a **parte web administrativa** como portal de operação.

---

## 1) Leitura do contexto atual do repositório

### O que já existe

- `app-servicopro`: app mobile (Expo/React Native) com fluxos de:
  - autenticação,
  - jornada de cliente (home, categorias, profissionais, solicitação, pagamento),
  - jornada de prestador (home, solicitações, perfil).
- `servicos-pro-app`: aplicação web (React/Vite), candidata natural para virar o **painel administrativo**.

### Conclusão de contexto

O frontend já modela entidades de negócio essenciais (usuário, profissional, categoria, solicitação e pagamento), então o backend deve ser desenhado com foco em:

1. **multi-perfil** (cliente, prestador, admin),
2. **ciclo de vida da solicitação de serviço**,
3. **pagamentos e repasses**,
4. **governança operacional via web administrativa**.

---

## 2) Escopo de produto do backend

## 2.1 Domínios principais

1. **Identidade e Acesso**
   - cadastro/login,
   - papéis (RBAC): `client`, `provider`, `admin`, `support`, `finance`.
2. **Catálogo e Descoberta**
   - categorias,
   - serviços ofertados por prestador,
   - disponibilidade/região de atendimento.
3. **Marketplace (Core)**
   - criação de solicitação,
   - matching/aceite,
   - acompanhamento da execução,
   - conclusão/cancelamento.
4. **Financeiro**
   - cobrança do cliente,
   - split de pagamento,
   - comissão da plataforma,
   - repasse ao prestador,
   - estornos.
5. **Avaliações e Qualidade**
   - nota/comentário,
   - reputação do prestador,
   - mediação de disputas.
6. **Administração (Web Admin)**
   - gestão de usuários e prestadores,
   - gestão de categorias/serviços,
   - monitoramento de solicitações,
   - auditoria, antifraude, suporte e financeiro.

## 2.2 Escopo MVP recomendado

- Autenticação + perfis de usuário.
- CRUD de categorias e serviços.
- Solicitação de serviço com estados controlados.
- Pagamento inicial (1 gateway) com comissão.
- Avaliação pós-serviço.
- Painel admin com visão operacional básica.

## 2.3 Escopo Pós-MVP

- Chat em tempo real.
- Agenda avançada do prestador.
- Promoções/cuponagem.
- Motor de recomendação.
- Múltiplos meios de pagamento e antifraude avançado.

---

## 3) Requisitos funcionais (por módulo)

## 3.1 Autenticação e conta

- Cadastro com validação de email/telefone.
- Login por email/senha (MVP) + opção futura OTP/social login.
- Recuperação de senha.
- Refresh token e revogação de sessão.
- KYC básico para prestador (documentos e validação).

## 3.2 Perfil de cliente

- Dados pessoais e endereços.
- Histórico de solicitações.
- Métodos de pagamento tokenizados.
- Favoritar profissionais.

## 3.3 Perfil de prestador

- Dados profissionais, bio, especialidades.
- Áreas atendidas (cidade/bairro/raio).
- Serviços e preços.
- Disponibilidade (agenda/slots).
- Documentação para aprovação.

## 3.4 Solicitações de serviço

- Cliente cria solicitação com:
  - categoria/serviço,
  - descrição,
  - data/hora desejada,
  - endereço,
  - faixa de preço opcional.
- Matching por categoria + proximidade + disponibilidade.
- Prestador aceita/recusa.
- Controle de status:
  - `draft` → `open` → `accepted` → `in_progress` → `completed` (ou `cancelled`/`disputed`).
- Linha do tempo de eventos da solicitação.

## 3.5 Pagamentos e repasses

- Pré-autorização/cobrança ao concluir (regra de negócio a definir).
- Comissão configurável por categoria/prestador.
- Split para conta da plataforma + prestador.
- Saques e repasses com status.
- Estorno parcial/total com trilha de auditoria.

## 3.6 Avaliações e reputação

- Cliente avalia prestador após serviço concluído.
- Nota de 1 a 5 + comentário.
- Cálculo de reputação com média ponderada e volume.
- Moderação de conteúdo (admin).

## 3.7 Backoffice administrativo (web)

- Dashboard (GMV, volume de ordens, taxa de conclusão, SLA).
- Gestão de usuários e bloqueios.
- Aprovação de prestadores.
- Gestão de catálogo (categorias e serviços).
- Gestão de solicitações (busca, filtros, timeline, ações).
- Gestão financeira (pagamentos, estornos, repasses).
- Central de suporte e disputas.

---

## 4) Requisitos não funcionais

- **Segurança**: JWT curto + refresh token rotativo; senhas com Argon2/Bcrypt.
- **LGPD**: consentimento, minimização de dados, retenção e exclusão.
- **Auditoria**: trilha imutável para ações críticas (financeiro/admin).
- **Escalabilidade**: arquitetura modular com filas para tarefas assíncronas.
- **Observabilidade**: logs estruturados + métricas + tracing.
- **Disponibilidade**: alvo inicial de 99.5% com deploy sem downtime.
- **Performance**: p95 de APIs críticas < 400ms em carga nominal.

---

## 5) Arquitetura proposta (backend)

## 5.1 Estilo arquitetural

**Modular Monolith primeiro**, com limites de domínio claros para evolução futura a microserviços somente quando houver necessidade real de escala organizacional/técnica.

## 5.2 Módulos internos

- `auth`
- `users`
- `providers`
- `catalog`
- `requests`
- `payments`
- `reviews`
- `notifications`
- `admin`
- `audit`

## 5.3 Stack sugerida

- **API**: **Spring Boot**.
- **Banco relacional**: PostgreSQL.
- **Cache/fila**: Redis + **Spring Batch**.
- **Mensageria (futuro)**: RabbitMQ/Kafka (apenas quando necessário).
- **Storage**: S3-compatible para documentos/imagens.
- **ORM**: Hibernate.
- **Auth**: JWT + refresh + RBAC.
- **Infra**: Docker + CI/CD + IaC (Terraform opcional).

> Alternativa válida: Java/Kotlin + **Spring Boot**, caso a equipe tenha maior senioridade nesse ecossistema.

---

## 6) Modelo de dados inicial (alto nível)

## 6.1 Entidades núcleo

- `users` (cliente/prestador/admin)
- `profiles_client`
- `profiles_provider`
- `provider_documents`
- `addresses`
- `categories`
- `services`
- `provider_services`
- `service_requests`
- `service_request_events`
- `payments`
- `payouts`
- `reviews`
- `disputes`
- `notifications`
- `audit_logs`

## 6.2 Relacionamentos-chave

- Usuário 1:N Endereços.
- Prestador N:N Serviços (via `provider_services`).
- Solicitação referencia cliente + prestador + serviço.
- Solicitação 1:N Eventos (timeline).
- Solicitação 1:1/N Pagamentos (dependendo do fluxo).
- Solicitação 1:1 Avaliação (cliente → prestador).

---

## 7) Contrato de API (primeira versão)

## 7.1 Público (app)

- `POST /v1/auth/signup`
- `POST /v1/auth/login`
- `POST /v1/auth/refresh`
- `GET /v1/categories`
- `GET /v1/providers?category=&lat=&lng=&radius=`
- `GET /v1/providers/:id`
- `POST /v1/requests`
- `GET /v1/requests/:id`
- `PATCH /v1/requests/:id/cancel`
- `POST /v1/requests/:id/accept` (prestador)
- `POST /v1/requests/:id/start`
- `POST /v1/requests/:id/complete`
- `POST /v1/payments/intents`
- `POST /v1/reviews`

## 7.2 Administrativo (web admin)

- `GET /v1/admin/users`
- `PATCH /v1/admin/users/:id/status`
- `GET /v1/admin/providers/pending`
- `POST /v1/admin/providers/:id/approve`
- `POST /v1/admin/providers/:id/reject`
- `GET /v1/admin/requests`
- `GET /v1/admin/finance/transactions`
- `POST /v1/admin/disputes/:id/resolve`

---

## 8) Regras de negócio críticas

1. **Status da solicitação é máquina de estados** (não permitir transição inválida).
2. **Prestador só aparece em busca se estiver ativo e aprovado**.
3. **Pagamento só é capturado conforme política definida** (ex.: ao concluir).
4. **Avaliação só após conclusão** e uma avaliação por solicitação.
5. **Ações admin sensíveis exigem auditoria** e controle de permissão.
6. **Cancelamento com janela e política de multa** parametrizável.

---

## 9) Segurança e compliance

- Criptografia em trânsito (TLS) e em repouso para dados sensíveis.
- Rate limit e proteção contra brute force.
- WAF/CDN na borda (produção).
- Segregação de segredos (Vault/Secrets Manager).
- Política LGPD:
  - base legal e consentimento,
  - exportação e exclusão de dados,
  - retenção de logs com prazo definido.

---

## 10) Integração com frontend atual

## 10.1 Mapeamento de telas para endpoints

- Login/Cadastro → `auth`.
- Lista de categorias/profissionais → `categories/providers`.
- Solicitação de serviço → `requests`.
- Tela de pagamento → `payments`.
- Tela de solicitações do prestador → `requests` filtradas por `provider_id`.
- Web administrativa atual deve consumir `/admin/*`.

## 10.2 Estratégia de contrato

- Publicar OpenAPI 3.1 como contrato oficial.
- Gerar SDK TypeScript para mobile/web admin.
- Versionamento de API em `/v1`.

---

## 11) Roadmap técnico sugerido

## Fase 0 — Fundação (1–2 semanas)

- Setup backend (Spring Boot + Hibernate + Postgres + Redis).
- Padrões de projeto, observabilidade, CI/CD, ambientes.

## Fase 1 — MVP Core (3–5 semanas)

- Auth + perfis + catálogo + solicitações (FSM).
- Painel admin mínimo (usuários/prestadores/solicitações).

## Fase 2 — Financeiro (2–4 semanas)

- Integração gateway pagamento.
- Comissão, split e estornos.
- Telas/admin financeiro.

## Fase 3 — Qualidade e escala (contínuo)

- Avaliações, disputas, notificações robustas.
- Hardening de segurança, antifraude, otimizações.

---

## 12) Critérios de pronto (Definition of Done) por feature

- Requisito funcional implementado.
- Testes unitários + integração cobrindo fluxos críticos.
- Documentação OpenAPI atualizada.
- Logs e métricas instrumentados.
- Revisão de segurança (quando envolver auth/financeiro).
- Feature flag (quando risco operacional for alto).

---

## 13) Backlog inicial priorizado (épicos)

1. Épico A: Identidade e acesso.
2. Épico B: Cadastro e aprovação de prestadores.
3. Épico C: Catálogo e descoberta.
4. Épico D: Solicitações e execução de serviço.
5. Épico E: Pagamentos e repasses.
6. Épico F: Avaliações e reputação.
7. Épico G: Web admin operacional.
8. Épico H: Observabilidade, segurança e compliance.

---

## 14) Recomendação final de arquitetura

Para começar com velocidade e manter qualidade:

- adotar **modular monolith** em **Spring Boot** + PostgreSQL,
- desenhar desde o início contratos claros (OpenAPI + DTOs),
- implementar máquina de estados para solicitações,
- tratar financeiro e auditoria como domínios de alta criticidade,
- usar a aplicação web como **backoffice admin** com RBAC forte.

Esse desenho permite lançar MVP rapidamente sem comprometer evolução para um backend mais distribuído no futuro.