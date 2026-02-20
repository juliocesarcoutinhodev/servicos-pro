# ServiçoPro — Marketplace de Serviços Profissionais

Aplicativo mobile desenvolvido com **React Native + Expo** para conectar clientes e prestadores de serviços profissionais.

---

## 📱 Sobre o Projeto

O ServiçoPro é uma plataforma marketplace que permite:

- **Clientes** — buscar, avaliar e contratar profissionais de diversas áreas
- **Prestadores** — oferecer serviços, gerenciar solicitações e acompanhar desempenho

O backend é uma API REST em **Java 25 + Spring Boot 3.5**, autenticação via **JWT + Refresh Token com rotação**, cookies HttpOnly e rate limiting.

---

## 🚀 Tecnologias

| Categoria | Tecnologia |
|-----------|-----------|
| Core | React Native 0.81.5, Expo ~54, TypeScript 5.9 |
| Roteamento | Expo Router ~6 (file-based routing) |
| Estilização | NativeWind 4.2, Tailwind CSS 3.4, expo-linear-gradient |
| HTTP / Auth | Axios 1.x, expo-secure-store 15.x |
| Estado global | React Context API |
| Ícones | Lucide React Native |
| Utilitários | clsx, tailwind-merge |

---

## 📁 Estrutura do Projeto

```
app-servicopro/
├── app/
│   ├── _layout.tsx                  # Root layout — monta o AuthProvider
│   ├── index.tsx                    # Redirect → /login
│   ├── login.tsx                    # Tela de login ✅ integrado
│   ├── signup.tsx                   # Tela de cadastro ✅ integrado
│   ├── forgot-password.tsx          # Esqueci minha senha ✅ integrado (OTP)
│   ├── verify-otp.tsx               # Verificação de código OTP ✅ integrado
│   ├── reset-password.tsx           # Redefinir senha ✅ integrado
│   ├── (client)/                    # Rotas autenticadas — role: CLIENT
│   │   ├── _layout.tsx
│   │   ├── home.tsx                 # Home do cliente ✅ categorias + profissionais reais
│   │   ├── profile.tsx              # Perfil do cliente ✅ dados reais da API
│   │   ├── categories.tsx           # Categorias de serviço 🚧 skeleton
│   │   ├── professionals.tsx        # Lista de profissionais ✅ integrado + filtro
│   │   ├── professional-profile.tsx # Perfil do profissional ✅ integrado
│   │   ├── service-request.tsx      # Solicitação de serviço 🚧 skeleton
│   │   └── payment.tsx              # Pagamento 🚧 skeleton
│   └── (provider)/                  # Rotas autenticadas — role: PROVIDER
│       ├── _layout.tsx
│       ├── home.tsx                 # Home do prestador ✅ nome real + atalhos
│       ├── profile.tsx              # Perfil do prestador ✅ dados reais + contagem de serviços
│       ├── my-services.tsx          # Gerenciar serviços ✅ CRUD integrado
│       └── requests.tsx             # Solicitações recebidas 🚧 skeleton
├── components/ui/
│   ├── Button.tsx                   # Botão reutilizável (variants + loading)
│   ├── Input.tsx                    # Input com label, erro, toggle de senha
│   └── Card.tsx                     # Card com sombra padrão
├── constants/
│   ├── config.ts                    # API_BASE_URL, rotas, STORAGE_KEYS, TTLs
│   └── categoryMeta.ts              # Mapa de ícone + cores por nome de categoria
├── context/
│   └── AuthContext.tsx              # AuthProvider + useAuth + route guard
├── services/
│   └── apiClient.ts                 # Axios + interceptors + todas as funções de API
├── types/
│   ├── auth.ts                      # DTOs de autenticação (espelham o backend)
│   └── index.ts                     # Tipos de domínio (ServiceCategory, ProviderService, ProviderProfile…)
└── utils/
    ├── cn.ts                        # Merge de classes Tailwind (clsx)
    └── apiError.ts                  # Extratores de erro, máscara de telefone
```

---

## 🔐 Autenticação

### Fluxo Completo

```
Cadastro / Login
    │
    ├─► POST /auth/signup → 201 → auto-login
    └─► POST /auth/login  → 200 → { accessToken, expiresIn }
                                   + Set-Cookie: refresh_token (HttpOnly)
                                        │
                            SecureStore.setItem(accessToken)
                                        │
                    ┌───────────────────▼───────────────────┐
                    │   Toda request usa Bearer <accessToken> │
                    └───────────────────┬───────────────────┘
                                        │ 401
                            POST /auth/refresh (via cookie)
                                        │
                              ┌─────────┴──────────┐
                              │ OK → novo token     │ Falhou → logout
                              │ repete request      │ limpa SecureStore
                              └─────────────────────┘
```

### Fluxo de Recuperação de Senha (OTP)

```
forgot-password.tsx
    └─► POST /auth/forgot-password { email }
        → 202 (sempre, independente de existir)

verify-otp.tsx
    └─► Usuário digita código de 6 dígitos recebido por email

reset-password.tsx
    └─► POST /auth/reset-password { email, code, newPassword }
        → 200 → redireciona para login
```

### Endpoints Integrados

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `POST` | `/api/v1/auth/signup` | — | Cadastro (`name`, `email`, `phone`, `password`, `role`) |
| `POST` | `/api/v1/auth/login` | — | Login — retorna `accessToken` + cookie `refresh_token` |
| `POST` | `/api/v1/auth/refresh` | Cookie | Rotação silenciosa (cookie-only, sem body) |
| `POST` | `/api/v1/auth/logout` | Cookie | Logout — revoga cookie no servidor |
| `GET`  | `/api/v1/auth/me` | Bearer | Dados do usuário autenticado |
| `POST` | `/api/v1/auth/forgot-password` | — | Envia código OTP por email |
| `POST` | `/api/v1/auth/reset-password` | — | Redefine senha com código OTP |

### Endpoints de Serviços e Prestadores

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `GET`  | `/api/v1/services/categories` | — | Lista todas as categorias |
| `POST` | `/api/v1/services/categories` | Bearer | Cria nova categoria |
| `GET`  | `/api/v1/providers` | — | Lista prestadores ativos (paginado, filtro por `categoryId`) |
| `GET`  | `/api/v1/providers/{id}` | — | Perfil público completo de um prestador |
| `GET`  | `/api/v1/providers/{id}/reviews` | — | Avaliações paginadas de um prestador |
| `GET`  | `/api/v1/providers/services` | Bearer | Lista serviços do prestador autenticado |
| `POST` | `/api/v1/providers/services` | Bearer | Cria novo serviço do prestador |
| `PUT`  | `/api/v1/providers/services/{id}` | Bearer | Atualiza serviço do prestador |
| `DELETE` | `/api/v1/providers/services/{id}` | Bearer | Remove serviço do prestador |

**Base URL produção:** `http://vps7348.integrator.host:8080`

### Formato de Resposta da API

```json
// Sucesso
{
  "timestamp": "2026-02-20T12:17:43Z",
  "status": 200,
  "message": "Perfil carregado com sucesso.",
  "data": { ... }
}

// Erro
{
  "timestamp": "2026-02-20T12:17:43Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados inválidos.",
  "path": "/api/v1/auth/signup",
  "details": { "email": "Email já cadastrado." }
}
```

### Armazenamento

| Item | Onde | Chave |
|------|------|-------|
| Access Token | `expo-secure-store` | `servicepro_access_token` |
| User (cache) | `expo-secure-store` | `servicepro_user` |
| Refresh Token | Cookie HttpOnly (servidor) | `refresh_token` |

> O refresh token **nunca fica exposto no JS** — gerenciado exclusivamente via cookie HttpOnly com `Path=/api/v1/auth/refresh`, `SameSite=Strict`, `Secure`, `Max-Age=604800`.

### TTLs

| Token | TTL |
|-------|-----|
| Access Token | 15 min (900s) |
| Refresh Token | 7 dias (604800s) |

### Rate Limiting (signup e login)

| Header | Descrição |
|--------|-----------|
| `X-RateLimit-Limit` | Máximo de requisições no período |
| `X-RateLimit-Remaining` | Requisições restantes |
| `X-RateLimit-Reset` | Timestamp de reset |
| `Retry-After` | Segundos para aguardar (em caso de 429) |

O app exibe a mensagem: *"Muitas tentativas. Aguarde X segundo(s) e tente novamente."*

### Route Guard

O hook `useProtectedRoute` dentro do `AuthContext`:
- Aguarda o bootstrap da sessão antes de qualquer redirect (evita flicker)
- Usuários **não autenticados** → `/login`
- Role `CLIENT` → `/(client)/home`
- Role `PROVIDER` → `/(provider)/home`

### Refresh Token Silencioso

O interceptor de resposta Axios em `services/apiClient.ts`:
- Detecta `401` em qualquer endpoint (exceto `/auth/*`)
- Chama `POST /auth/refresh` **uma única vez** (flag `isRefreshing`)
- Enfileira todas as requests concorrentes que chegam durante o refresh
- Reexecuta a fila com o novo token após o refresh concluir
- Se o refresh falhar → limpa `SecureStore` e propaga o erro (AuthContext faz logout)

---

## 📺 Telas Implementadas

### Auth
| Tela | Arquivo | Status |
|------|---------|--------|
| Login | `app/login.tsx` | ✅ Integrado |
| Cadastro | `app/signup.tsx` | ✅ Integrado |
| Esqueci minha senha | `app/forgot-password.tsx` | ✅ Integrado (OTP) |
| Verificar OTP | `app/verify-otp.tsx` | ✅ Integrado |
| Redefinir senha | `app/reset-password.tsx` | ✅ Integrado |

### Cliente (role: CLIENT)
| Tela | Arquivo | Status |
|------|---------|--------|
| Home | `(client)/home.tsx` | ✅ Categorias e profissionais reais do backend |
| Perfil | `(client)/profile.tsx` | ✅ Dados reais da API `/me` |
| Categorias | `(client)/categories.tsx` | 🚧 Skeleton |
| Lista de profissionais | `(client)/professionals.tsx` | ✅ Integrado — filtro por categoria, busca e ordenação |
| Perfil do profissional | `(client)/professional-profile.tsx` | ✅ Integrado — serviços e avaliações reais |
| Solicitar serviço | `(client)/service-request.tsx` | 🚧 Skeleton |
| Pagamento | `(client)/payment.tsx` | 🚧 Skeleton |

### Prestador (role: PROVIDER)
| Tela | Arquivo | Status |
|------|---------|--------|
| Home | `(provider)/home.tsx` | ✅ Nome real + atalho para Meus Serviços |
| Perfil | `(provider)/profile.tsx` | ✅ Dados reais + contagem de serviços clicável |
| Meus Serviços | `(provider)/my-services.tsx` | ✅ CRUD completo integrado |
| Solicitações | `(provider)/requests.tsx` | 🚧 Skeleton |

---

## 🗺️ Mapa de Categorias (`constants/categoryMeta.ts`)

Arquivo central que associa o **nome da categoria** (vindo do backend) a um **ícone Lucide** e **cores de gradiente**. Usado em todas as telas que exibem categorias.

```ts
// Para adicionar uma nova categoria, basta adicionar uma entrada:
"pintor": { icon: PaintBucket, colors: ["#3B82F6", "#6366F1"] }
```

Categorias mapeadas: `eletricista`, `encanador`, `diarista`, `pintor`, `montador de moveis`, `jardineiro`, `tecnico de ar-condicionado`, `marido de aluguel`, `mecanica`, `cabeleireiro`. Qualquer categoria não mapeada recebe ícone e cor padrão (genérico).

---

## 🛠️ Instalação e Execução

```bash
# Instalar dependências
pnpm install

# Iniciar servidor de desenvolvimento (Expo Go / tunnel)
pnpm start

# Plataformas específicas
pnpm android
pnpm ios
pnpm web
```

> **Requisito:** Node 20+, pnpm 9+, Expo CLI instalado globalmente.

---

## 🎨 Componentes UI

### Button

```tsx
<Button
  variant="primary"   // primary | secondary | outline | ghost
  size="lg"           // sm | md | lg
  onPress={fn}
  fullWidth
  loading={isSubmitting}
  disabled={isSubmitting}
>
  Entrar
</Button>
```

### Input

```tsx
<Input
  label="Email"
  placeholder="seu@email.com"
  keyboardType="email-address"
  autoCapitalize="none"
  value={email}
  onChangeText={setEmail}
  error={errors.email}
  showPasswordToggle   // apenas para campos de senha
/>
```

### Card

```tsx
<Card variant="elevated" className="mb-4">
  <Text>Conteúdo</Text>
</Card>
```

---

## 🔧 Utilitários (`utils/apiError.ts`)

| Função | Descrição |
|--------|-----------|
| `extractApiError(err)` | Extrai `message` do envelope de erro da API. Trata 429 com `Retry-After`. |
| `extractFieldErrors(err)` | Extrai `details` (mapa de campos) para erros de validação. |
| `isRateLimitError(err)` | Retorna `true` se o erro for HTTP 429. |
| `formatPhoneMask(value)` | Aplica máscara brasileira em tempo real: `(11) 99999-9999`. |
| `formatPhoneToE164(raw)` | Converte número mascarado/raw para E.164: `+5511999999999`. |

---

## ✅ Status das Funcionalidades

### Concluído
- [x] Cadastro (CLIENT / PROVIDER) com validação local e erros da API
- [x] Máscara de telefone automática `(11) 99999-9999` → E.164
- [x] Login com redirect automático por `role`
- [x] Armazenamento seguro do access token (`expo-secure-store`)
- [x] Refresh token silencioso com token rotation (interceptor Axios)
- [x] Route guard baseado em `role` (CLIENT / PROVIDER)
- [x] Bootstrap de sessão ao iniciar o app (sem flicker)
- [x] Tratamento de erros: validação (422), credenciais inválidas (401), rate limit (429)
- [x] Esqueci minha senha com envio de código OTP por email
- [x] Verificação de OTP e redefinição de senha
- [x] Tela de perfil do **Cliente** com dados reais do `/auth/me`
- [x] Tela de perfil do **Prestador** com dados reais do `/auth/me` + contagem de serviços
- [x] Nome real do usuário nas homes (CLIENT e PROVIDER)
- [x] Pull-to-refresh em todas as telas com dados dinâmicos
- [x] Logout com confirmação e limpeza de cookie + SecureStore
- [x] Safe area correta (gradiente edge-to-edge sem faixa branca)
- [x] **Home do cliente** com categorias reais do backend
- [x] **Home do cliente** com profissionais disponíveis reais do backend
- [x] **Lista de profissionais** filtrada por categoria, busca por nome e ordenação
- [x] **Perfil público do profissional** com serviços e avaliações reais
- [x] **CRUD de serviços do prestador** — criar, editar, remover com picker de categoria
- [x] Mapa de ícones/cores por categoria (`constants/categoryMeta.ts`)
- [x] Skeletons de loading nas telas de listagem
- [x] Estados de erro com retry em todas as telas de dados

### Próximas Etapas
- [ ] Tela de categorias do cliente integrada ao backend
- [ ] Solicitação de serviço (fluxo CLIENT → PROVIDER)
- [ ] Aceite/recusa de solicitações (PROVIDER)
- [ ] Sistema de avaliações (cliente avalia prestador após conclusão)
- [ ] Edição de perfil (bio, foto)
- [ ] Notificações push
- [ ] Chat em tempo real
- [ ] Upload de imagem de perfil
- [ ] Geolocalização ("profissionais próximos" com distância real)
- [ ] Histórico de serviços realizados
- [ ] Pagamento integrado

---

## 📋 Changelog

### v1.5.0 (atual) — 2026-02-20
- ✨ **Home do cliente** — categorias reais do backend com ícones/cores mapeados por nome
- ✨ **Home do cliente** — profissionais disponíveis reais do backend com avatar em iniciais
- ✨ **Lista de profissionais** (`professionals.tsx`) — integrado com `GET /api/v1/providers`, filtro por `categoryId`, busca por nome, ordenação (A–Z / melhor avaliados / mais serviços)
- ✨ **Perfil do profissional** (`professional-profile.tsx`) — integrado com `GET /api/v1/providers/{id}` e `GET /api/v1/providers/{id}/reviews`, exibe serviços reais com preço formatado, avaliações com estrelas, bio, stats
- ✨ **CRUD de serviços do prestador** (`my-services.tsx`) — tela nova com criação, edição e remoção de serviços via `POST/PUT/DELETE /api/v1/providers/services`, picker de categoria, validação de preço em centavos
- ✨ **Perfil do prestador** — card "Serviços" agora mostra contagem real e navega para `my-services`
- ✨ `constants/categoryMeta.ts` — mapa central de ícone + gradiente por nome de categoria
- ✨ Novos tipos: `ServiceCategory`, `ProviderService`, `ProviderSummary`, `ProviderProfile`, `ProviderProfileService`, `ProviderReview`, `Page<T>`
- ✨ Novas funções em `apiClient.ts`: `listServiceCategories`, `createServiceCategory`, `listMyProviderServices`, `createProviderService`, `updateProviderService`, `deleteProviderService`, `listProviders`, `getProviderProfile`, `listProviderReviews`
- ✨ Skeletons de loading nas listagens de categorias e profissionais
- 🐛 Corrigido "Sem categoria" nos cards de serviço — enriquecimento client-side via `categoryId` + mapa de categorias

### v1.4.0 — 2026-02-20
- ✨ Tela de perfil do **Cliente** com dados reais do `GET /auth/me`
- ✨ Tela de perfil do **Prestador** com dados reais do `GET /auth/me`
- ✨ Nome real do usuário nas homes (CLIENT e PROVIDER) via `useAuth()`
- ✨ Logout com `Alert` de confirmação em ambas as telas de perfil
- ✨ Pull-to-refresh nas telas de perfil
- ✨ Estados de loading e erro com UI dedicada
- 🐛 Corrigido faixa branca no topo das telas de perfil (`SafeAreaView` → `View` + `useSafeAreaInsets`)
- ✨ `AuthUser` atualizado com `createdAt` e `active` para espelhar o backend
- ✨ Formatação de telefone E.164 → `(11) 99470-4876` nas telas de perfil
- ✨ Formatação de data ISO → `20 de fevereiro de 2026` nas telas de perfil

### v1.3.0 — 2026-02-20
- ✨ `login.tsx` simplificado — removido seletor de tipo redundante (role vem do JWT)
- ✨ Ícones nos campos de login e cadastro (Mail, Lock, User, Phone)
- ✨ Fluxo completo de recuperação de senha via OTP (`forgot-password` → `verify-otp` → `reset-password`)
- ✨ `signup.tsx` — máscara de telefone automática em tempo real
- ✨ `utils/apiError.ts` — tratamento de 429 com `Retry-After`, `formatPhoneMask`, `isRateLimitError`
- 🐛 Validação de telefone ajustada para 10–11 dígitos (formato BR)

### v1.2.0 — 2026-02-19
- ✨ Integração completa de autenticação com o backend Java/Spring Boot
- ✨ `AuthContext` com route guard automático por role
- ✨ `apiClient.ts` com interceptor de refresh token silencioso e token rotation
- ✨ `types/auth.ts` — DTOs tipados espelhando o contrato do backend
- ✨ `constants/config.ts` — configuração centralizada de URLs e chaves
- ➕ Dependências: `axios`, `expo-secure-store`

### v1.1.0
- ✨ Gerenciamento inteligente de teclado (login e signup)
- ✨ Toggle de visibilidade de senha
- ✨ Gradiente azul padronizado

### v1.0.0
- 🎉 Setup inicial React Native / Expo
- 🎉 Todas as telas de UI (skeleton)
- 🎉 NativeWind v4, Expo Router configurados

---

> **Backend:** Java 25 + Spring Boot 3.5 — repositório separado.
> Coleções Postman disponíveis em `backend/docs/postman/`.
