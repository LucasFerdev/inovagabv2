# Plano de Implementação — App Águia Branca

Este plano detalha a construção do aplicativo corporativo **Águia Branca**, focado em Operadores, Gestores e Liderança, seguindo os princípios de Clean Architecture, MVVM e um Design System customizado.

## User Review Required

> [!IMPORTANT]
> A implementação será incremental. Vou solicitar sua validação ao final de etapas críticas, como a finalização do Design System e a navegação baseada em perfis.

> [!WARNING]
> Utilizaremos dados mockados nesta Sprint 1 para garantir a agilidade e validação da UX/UI antes da integração com APIs reais.

## Proposed Changes

O projeto será estruturado em camadas para garantir escalabilidade e testabilidade.

### [Component Name] Core & Architecture

#### [MODIFY] [build.gradle.kts](file:///D:/mobileproject/InovaGABv2/app/build.gradle.kts)
Adição de dependências: Hilt, Navigation, DataStore, Room, Retrofit, Kotlin Serialization.

#### [NEW] Estrutura de Pacotes
Criação dos pacotes:
- `core/` (navigation, ui, designsystem, session, etc.)
- `data/` (local, remote, repository, models)
- `domain/` (model, repository, usecase)
- `presentation/` (auth, operator, manager, leadership)

---

### [Component Name] Design System Águia Branca

#### [NEW] [AguiaColors.kt](file:///D:/mobileproject/InovaGABv2/app/src/main/java/br/com/inovagabv2/core/designsystem/AguiaColors.kt)
Definição da paleta corporativa (Navy, Azul Principal, Verde, Roxo, etc.).

#### [NEW] [AguiaTypography.kt](file:///D:/mobileproject/InovaGABv2/app/src/main/java/br/com/inovagabv2/core/designsystem/AguiaTypography.kt)
Hierarquia tipográfica baseada no Material 3.

#### [NEW] Componentes Reutilizáveis
Implementação de `AguiaButton`, `AguiaCard`, `AguiaIdeaCard`, `AguiaMetricCard`, etc.

---

### [Component Name] Auth & Session

#### [NEW] [SessionManager.kt](file:///D:/mobileproject/InovaGABv2/app/src/main/java/br/com/inovagabv2/core/session/SessionManager.kt)
Gerenciamento de sessão usando DataStore (ID, Nome, Role).

#### [NEW] Login Mockado
Tela de login com usuários pré-definidos para cada perfil.

---

### [Component Name] Navegação e Perfis

#### [NEW] [AguiaNavigation.kt](file:///D:/mobileproject/InovaGABv2/app/src/main/java/br/com/inovagabv2/core/navigation/AguiaNavigation.kt)
Fluxo de navegação condicional baseado no `Role` do usuário logado.

---

### [Component Name] Funcionalidades por Perfil

#### [NEW] Operador
Telas de Home, Estratégia, Criação de Ideias, Minhas Ideias e Detalhes.

#### [NEW] Gestor
Telas de Home, Análise de Ideias, Detalhes da Ideia, Projetos e Detalhes do Projeto.

#### [NEW] Liderança
Dashboard Executivo (ROI, Lucro, etc.), Gestão de Estratégias e Resultados.

---

## Verification Plan

### Automated Tests
- Testes unitários para `SessionManager` e controle de permissões por `Role`.
- Testes de mapeamento de dados mockados.

### Manual Verification
- Validação visual de cada perfil no emulador.
- Verificação de responsividade em diferentes tamanhos de tela (Compact, Medium, Expanded).
- Fluxo de logout e persistência de sessão.
