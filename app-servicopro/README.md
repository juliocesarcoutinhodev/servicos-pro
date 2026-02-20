# ServiçoPro - Marketplace de Serviços Profissionais

Aplicativo mobile desenvolvido com React Native e Expo para conectar clientes e prestadores de serviços profissionais.

## 📱 Sobre o Projeto

O ServiçoPro é uma plataforma marketplace que permite:
- **Clientes**: Buscar, avaliar e contratar profissionais de diversas áreas
- **Prestadores**: Oferecer serviços, gerenciar solicitações e acompanhar desempenho

## 🚀 Tecnologias

| Categoria | Tecnologia |
|---|---|
| Core | React Native 0.81.5, Expo ~54, TypeScript 5.9 |
| Roteamento | Expo Router ~6 (file-based) |
| Estilização | NativeWind 4.2, Tailwind CSS 3.4, expo-linear-gradient |
| HTTP / Auth | Axios 1.x, expo-secure-store 15.x |
| Estado | React Context API |
| Ícones | Lucide React Native |
| Utilitários | clsx, tailwind-merge |

## 📁 Estrutura do Projeto

```
app-servicopro/
├── app/
│   ├── _layout.tsx              # Root layout (AuthProvider wraps here)
│   ├── index.tsx                # Redirect → /login
│   ├── login.tsx                # Tela de login (integrado à API)
│   ├── signup.tsx               # Tela de cadastro (integrado à API)
│   ├── (client)/                # Rotas autenticadas — role: CLIENT
│   │   ├── home.tsx
│   │   ├── categories.tsx
│   │   ├── professionals.tsx
│   │   ├── professional-profile.tsx
│   │   ├── service-request.tsx
│   │   └── payment.tsx
│   └── (provider)/              # Rotas autenticadas — role: PROVIDER
│       ├── home.tsx
│       ├── requests.tsx
│       └── profile.tsx
├── components/ui/               # Componentes reutilizáveis
│   ├── Button.tsx
│   ├── Input.tsx
│   └── Card.tsx
├── constants/
│   └── config.ts                # API_BASE_URL, rotas, storage keys
├── context/
│   └── AuthContext.tsx          # AuthProvider + useAuth hook + route guard
├── services/
│   └── apiClient.ts             # Axios instance + interceptors + auth services
├── types/
│   ├── index.ts                 # Tipos de domínio (User, Professional, etc.)
│   └── auth.ts                  # DTOs de autenticação (espelham o backend)
└── utils/
    ├── cn.ts                    # Merge de classes Tailwind
    └── apiError.ts              # Extratores de erro da API
```

## 🔐 Autenticação

### Visão Geral

A autenticação segue o fluxo completo implementado no backend Java/Spring Boot:

```
Login → accessToken (SecureStore) + refresh_token (HttpOnly cookie)
         ↓
Chamadas autenticadas → Bearer <accessToken>
         ↓
401 → interceptor tenta POST /auth/refresh automaticamente
         ↓
Refresh ok → novo token salvo, request repetida
Refresh falhou → SecureStore limpo, usuário redirecionado ao login
```

### Endpoints Integrados

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/auth/signup` | Cadastro (role: CLIENT \| PROVIDER) |
| `POST` | `/api/v1/auth/login` | Login — retorna accessToken + cookie |
| `POST` | `/api/v1/auth/refresh` | Rotação de token (cookie-only) |
| `POST` | `/api/v1/auth/logout` | Logout — limpa cookie no servidor |
| `GET`  | `/api/v1/auth/me` | Dados do usuário autenticado |

**Base URL (Produção):** `http://vps7348.integrator.host:8080`

### Formato de Resposta da API

```json
// Sucesso
{ "timestamp": "", "status": 200, "message": "", "data": {} }

// Erro
{ "timestamp": "", "status": 400, "error": "", "message": "", "path": "", "details": {} }
```

### Armazenamento de Token

| Item | Onde | Chave |
|------|------|-------|
| Access Token | `expo-secure-store` | `servicepro_access_token` |
| User (cache) | `expo-secure-store` | `servicepro_user` |
| Refresh Token | Cookie HttpOnly (servidor) | `refresh_token` |

> **Nota**: O refresh token nunca fica exposto no JS — é gerenciado exclusivamente via cookie HttpOnly com `Path=/api/v1/auth/refresh`.

### Token TTLs

| Token | TTL |
|-------|-----|
| Access Token | 15 min (900s) |
| Refresh Token | 7 dias (604800s) |

### Route Guard

O `AuthContext` contém um hook `useProtectedRoute` que:
- Redireciona usuários não autenticados para `/login`
- Redireciona `CLIENT` para `/(client)/home`
- Redireciona `PROVIDER` para `/(provider)/home`
- Aguarda o bootstrap da sessão antes de redirecionar (evita flicker)

### Refresh Token Silencioso

O interceptor de resposta do Axios (em `services/apiClient.ts`) detecta respostas `401`, chama `POST /auth/refresh` **uma única vez** (flag `isRefreshing`), enfileira todas as requests concorrentes, e as reexecuta com o novo token após o refresh completar.

## 🛠️ Instalação

```bash
# Instalar dependências
pnpm install

# Iniciar servidor de desenvolvimento
pnpm start

# Android / iOS / Web
pnpm android
pnpm ios
pnpm web
```

## 🎨 Componentes UI

### Button
```tsx
<Button variant="primary" size="lg" onPress={fn} fullWidth loading={false}>
  Entrar
</Button>
// variants: primary | secondary | outline | ghost
// sizes: sm | md | lg
```

### Input
```tsx
<Input
  label="Email"
  placeholder="seu@email.com"
  keyboardType="email-address"
  error={errors.email}
  value={email}
  onChangeText={setEmail}
/>
// showPasswordToggle — mostra botão olho para campos de senha
```

## ✅ Status das Funcionalidades

### Implementado
- [x] Cadastro de usuário (CLIENT / PROVIDER) com validação
- [x] Login com integração real à API
- [x] Armazenamento seguro do access token (`expo-secure-store`)
- [x] Refresh token automático e silencioso (interceptor Axios)
- [x] Token rotation — novo cookie a cada refresh
- [x] Route guard baseado em role
- [x] Bootstrap de sessão ao iniciar o app
- [x] Tratamento de erros da API com mensagens contextuais
- [x] Formatação de telefone para E.164 (`+55...`)
- [x] Todas as telas de UI (cliente e prestador)

### Próximas Etapas
- [ ] Integração das telas de cliente com endpoints de profissionais
- [ ] Integração das telas de prestador com endpoints de solicitações
- [ ] Notificações push
- [ ] Geolocalização
- [ ] Upload de imagem de perfil
- [ ] Chat em tempo real
- [ ] Sistema de avaliações

## 📋 Changelog

### v1.2.0 (Atual)
- ✨ Integração completa de autenticação com o backend Java/Spring Boot
- ✨ `AuthContext` com route guard automático por role
- ✨ `apiClient.ts` com interceptor de refresh token silencioso e token rotation
- ✨ `signup.tsx` refatorado com seleção de role, validação real e tratamento de erros da API
- ✨ `login.tsx` refatorado com lógica real, erros inline e estado de loading
- ✨ `types/auth.ts` — DTOs tipados espelhando o contrato do backend
- ✨ `constants/config.ts` — configuração centralizada de URLs e chaves
- ✨ `utils/apiError.ts` — extratores de erro/campo da API padronizados
- ➕ Dependências: `axios`, `expo-secure-store`

### v1.1.0
- ✨ Gerenciamento inteligente de teclado (login e signup)
- ✨ Toggle de visibilidade de senha
- ✨ Gradiente azul padronizado no topo das telas

### v1.0.0
- 🎉 Migração inicial para React Native / Expo
- 🎉 Todas as telas de UI implementadas
- 🎉 NativeWind v4, Expo Router configurados

---

