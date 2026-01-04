# ServiçoPro - Marketplace de Serviços Profissionais

Aplicativo mobile desenvolvido com React Native e Expo para conectar clientes e prestadores de serviços profissionais. O projeto foi migrado de React Web para React Native seguindo as melhores práticas de desenvolvimento e clean code.

## 📱 Sobre o Projeto

O ServiçoPro é uma plataforma marketplace que permite:
- **Clientes**: Buscar, avaliar e contratar profissionais de diversas áreas
- **Prestadores**: Oferecer serviços, gerenciar solicitações e acompanhar desempenho

## 🚀 Tecnologias Utilizadas

### Core
- **React Native** 0.81.5
- **Expo** ~54.0.30
- **Expo Router** ~6.0.21 (File-based routing)
- **TypeScript** 5.9.2

### Estilização
- **NativeWind** 4.2.1 (Tailwind CSS para React Native)
- **Tailwind CSS** 3.4.1
- **expo-linear-gradient** 15.0.8
- **lucide-react-native** 0.562.0 (Ícones)

### Navegação e UI
- **React Navigation** 7.x
- **react-native-safe-area-context** 5.6.0
- **react-native-reanimated** 4.1.1
- **react-native-gesture-handler** 2.28.0

### Utilitários
- **clsx** 2.1.1
- **tailwind-merge** 3.4.0
- **@react-native-async-storage/async-storage** 2.2.0

## 📁 Estrutura do Projeto

```
app-servicopro/
├── app/                          # Rotas (Expo Router)
│   ├── _layout.tsx              # Layout raiz
│   ├── index.tsx                # Tela inicial (redirect)
│   ├── login.tsx                 # Tela de login
│   ├── signup.tsx                # Tela de cadastro
│   ├── (client)/                 # Grupo de rotas do cliente
│   │   ├── _layout.tsx
│   │   ├── home.tsx              # Home do cliente
│   │   ├── categories.tsx         # Lista de categorias
│   │   ├── professionals.tsx     # Lista de profissionais
│   │   ├── professional-profile.tsx  # Perfil do profissional
│   │   ├── service-request.tsx   # Solicitar serviço
│   │   └── payment.tsx           # Tela de pagamento
│   └── (provider)/               # Grupo de rotas do prestador
│       ├── _layout.tsx
│       ├── home.tsx              # Dashboard do prestador
│       ├── requests.tsx           # Gerenciar solicitações
│       └── profile.tsx           # Perfil do prestador
├── components/
│   └── ui/                       # Componentes reutilizáveis
│       ├── Button.tsx            # Botão com variantes
│       ├── Input.tsx             # Input com label e erro
│       └── Card.tsx              # Card com variantes
├── types/
│   └── index.ts                  # Tipos TypeScript
├── utils/
│   └── cn.ts                     # Utilitário para merge de classes
├── global.css                    # Estilos globais Tailwind
├── tailwind.config.js            # Configuração Tailwind
├── babel.config.js               # Configuração Babel
├── metro.config.js               # Configuração Metro
└── nativewind-env.d.ts           # Tipos NativeWind
```

## 🛠️ Instalação e Configuração

### Pré-requisitos
- Node.js 18+ 
- npm ou yarn
- Expo CLI (`npm install -g expo-cli`)
- Android Studio (para Android) ou Xcode (para iOS)

### Instalação

1. Clone o repositório:
```bash
git clone <repository-url>
cd app-servicopro
```

2. Instale as dependências:
```bash
npm install
```

3. Inicie o servidor de desenvolvimento:
```bash
npx expo start
```

4. Para limpar o cache (se necessário):
```bash
npx expo start --clear
```

### Scripts Disponíveis

```bash
npm start          # Inicia o servidor Expo
npm run android    # Inicia no Android
npm run ios        # Inicia no iOS
npm run web        # Inicia no navegador
npm run lint       # Executa o linter
```

## 📱 Telas Implementadas

### Autenticação
- ✅ **Login** - Tela de login com:
  - Seleção de tipo de usuário (Cliente/Prestador)
  - Preenchimento automático de email baseado no tipo selecionado
  - Gerenciamento inteligente de teclado
  - Toggle de visibilidade de senha
  - Scroll automático quando campos são focados
- ✅ **Signup** - Tela de cadastro com:
  - Gerenciamento de teclado
  - Toggle de visibilidade de senha
  - Scroll automático para campos de senha

### Fluxo do Cliente
- ✅ **Home** - Dashboard com categorias e profissionais próximos
- ✅ **Categories** - Lista completa de categorias de serviços
- ✅ **Professionals** - Lista de profissionais com filtros
- ✅ **Professional Profile** - Perfil detalhado do profissional
- ✅ **Service Request** - Formulário de solicitação de serviço
- ✅ **Payment** - Tela de pagamento com múltiplas opções

### Fluxo do Prestador
- ✅ **Home** - Dashboard com estatísticas e solicitações
- ✅ **Requests** - Gerenciamento de solicitações de serviços
- ✅ **Profile** - Perfil e configurações do prestador

## 🎨 Componentes UI

### Button
Componente de botão com múltiplas variantes:
- `primary` - Botão principal (azul)
- `secondary` - Botão secundário (cinza)
- `outline` - Botão com borda
- `ghost` - Botão transparente

```tsx
<Button 
  variant="primary" 
  size="lg" 
  onPress={handlePress}
  fullWidth
>
  Entrar
</Button>
```

### Input
Input com label, tratamento de erros e toggle de senha:
```tsx
<Input
  label="Email"
  placeholder="Digite seu email"
  keyboardType="email-address"
  error={errors.email}
/>

<Input
  label="Senha"
  placeholder="Digite sua senha"
  secureTextEntry
  showPasswordToggle  // Mostra botão para alternar visibilidade
/>
```

### Card
Card com variantes de estilo:
- `default` - Card padrão
- `elevated` - Card com sombra maior
- `outlined` - Card com borda destacada

```tsx
<Card variant="elevated">
  <Text>Conteúdo do card</Text>
</Card>
```

## ⚙️ Configurações

### NativeWind v4
O projeto utiliza NativeWind v4 para estilização com Tailwind CSS. As configurações principais estão em:

- **babel.config.js**: Configurado com `jsxImportSource: "nativewind"`
- **metro.config.js**: Configurado com `withNativeWind`
- **tailwind.config.js**: Preset do NativeWind e cores customizadas
- **global.css**: Importado no `_layout.tsx` raiz

### Expo Router
Navegação baseada em arquivos:
- Rotas agrupadas em `(client)` e `(provider)`
- Layouts aninhados para cada grupo
- Navegação type-safe com TypeScript

### TypeScript
- Strict mode habilitado
- Tipos centralizados em `/types`
- Path aliases configurados (`@/*`)

## 🎯 Funcionalidades Implementadas

### ✅ Completas
- [x] Navegação entre telas
- [x] Interface responsiva
- [x] Componentes reutilizáveis
- [x] Tipagem TypeScript
- [x] Estilização com Tailwind/NativeWind
- [x] Gradientes e animações básicas
- [x] Ícones com Lucide React Native
- [x] Gerenciamento de teclado (KeyboardAvoidingView)
- [x] Scroll automático quando teclado aparece
- [x] Toggle de visibilidade de senha
- [x] Gradiente azul consistente no topo de todas as telas
- [x] SafeAreaView configurado corretamente
- [x] StatusBar configurada para cada tela

### 🚧 Próximas Implementações
- [ ] Autenticação real (backend)
- [ ] Validação de formulários (react-hook-form)
- [ ] Integração com API
- [ ] Persistência de dados (AsyncStorage)
- [ ] Loading states
- [ ] Error handling
- [ ] Animações avançadas (Reanimated)
- [ ] Push notifications
- [ ] Geolocalização
- [ ] Upload de imagens
- [ ] Sistema de avaliações
- [ ] Chat em tempo real

## 🏗️ Arquitetura

### Princípios Aplicados
- **Clean Code**: Código limpo e legível
- **Separation of Concerns**: Separação de responsabilidades
- **DRY**: Reutilização de componentes
- **Type Safety**: TypeScript em todo o projeto
- **Component Composition**: Componentes pequenos e focados

### Padrões Utilizados
- File-based routing (Expo Router)
- Component composition
- Custom hooks (quando necessário)
- Type-safe navigation
- Centralized types

## 🎨 Melhorias de UX Implementadas

### Gerenciamento de Teclado
- **KeyboardAvoidingView**: Ajusta o layout automaticamente quando o teclado aparece
- **Scroll automático**: Campos de entrada rolam automaticamente para ficarem visíveis
- **Padding dinâmico**: Espaçamento inferior ajustado baseado na visibilidade do teclado
- **Listeners de teclado**: Detecta quando o teclado aparece/desaparece para ajustar o scroll

### Interface Consistente
- **Gradiente azul no topo**: Todas as telas com header azul começando do topo absoluto
- **StatusBar configurada**: StatusBar com estilo claro sobre gradientes azuis
- **SafeAreaView**: Respeita áreas seguras do dispositivo (notch, home indicator)
- **Transições suaves**: Scroll animado quando o teclado aparece/desaparece

### Componentes Aprimorados
- **Input com toggle de senha**: Botão para mostrar/ocultar senha enquanto digita
- **Feedback visual**: Indicadores visuais para tipo de usuário selecionado
- **Acessibilidade**: Campos focáveis e navegáveis com teclado

## 📚 Recursos e Documentação

- [Expo Documentation](https://docs.expo.dev/)
- [Expo Router](https://docs.expo.dev/router/introduction/)
- [NativeWind](https://www.nativewind.dev/)
- [React Native](https://reactnative.dev/)
- [TypeScript](https://www.typescriptlang.org/)

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto é privado.

## 👨‍💻 Desenvolvido com

- React Native
- Expo
- TypeScript
- NativeWind (Tailwind CSS)
- Clean Code & Best Practices

## 📋 Changelog

### Versão 1.1.0 (Atual)
- ✨ Adicionado gerenciamento inteligente de teclado nas telas de login e signup
- ✨ Implementado toggle de visibilidade de senha nos campos de senha
- ✨ Padronizado gradiente azul no topo de todas as telas
- ✨ Configurado StatusBar para cada tela
- ✨ Melhorado scroll automático quando campos são focados
- 🐛 Corrigido espaçamento branco no topo das telas
- 🐛 Corrigido problema de campos escondidos atrás do teclado

### Versão 1.0.0
- 🎉 Migração inicial do projeto React Web para React Native
- 🎉 Implementação de todas as telas principais
- 🎉 Configuração de NativeWind v4
- 🎉 Estrutura de navegação com Expo Router

---

**Versão**: 1.1.0  
**Última atualização**: Janeiro 2025
