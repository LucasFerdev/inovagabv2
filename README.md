# InovaGAB — Gestão de Inovação Águia Branca

Plataforma corporativa móvel para gestão de sugestões, diretrizes estratégicas e indicadores de impacto, focada no setor de transporte rodoviário de passageiros da **Viação Águia Branca**.

Projeto focado em produtividade, experiência do usuário e inovação corporativa — **Desenvolvimento Android Nativo com Jetpack Compose**.

---

## Sumário
* [Sobre o Projeto](#sobre-o-projeto)
* [Tecnologias Utilizadas](#tecnologias-utilizadas)
* [Funcionalidades e Telas](#funcionalidades-e-telas)
* [Perfis de Acesso](#perfis-de-acesso)
* [Arquitetura](#arquitetura)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Como Executar](#como-executar)
* [Boas Práticas e Padrões](#boas-práticas-e-padrões)
* [Autor](#autor)

---

## Sobre o Projeto

O **InovaGAB** é um ecossistema mobile corporativo que conecta desde os colaboradores da operação até a diretoria executiva da empresa. O objetivo é democratizar a inovação, permitindo que colaboradores da ponta enviem sugestões de melhoria enquanto gestores e diretores acompanham projetos e indicadores financeiros/operacionais em tempo real.

O projeto simula o fluxo completo de inovação corporativa:
1. **Onboarding & Autenticação:** Apresentação da proposta de valor, criação de conta e login unificado com suporte a atalhos rápidos para teste de perfis.
2. **Operador:** Consulta orientações estratégicas, cadastra sugestões via assistente em etapas (3 steps) com escolha de categorias e acompanha a evolução via linha do tempo.
3. **Gestor:** Analisa ideias pendentes, define prioridades, gerencia portfólio de projetos ativos com acompanhamento dos responsáveis.
4. **Liderança:** Dashboard executivo de alto nível com métricas de impacto, ROI Global, lucro gerado, redução de custos em gráficos *sparklines* e mensuração de resultados por projeto.

---

## Tecnologias Utilizadas

### Mobile Stack
| Categoria | Tecnologia |
| :--- | :--- |
| **Linguagem** | **Kotlin** (100% Kotlin) |
| **Interface** | **Jetpack Compose** (Declarativo) |
| **Design System** | **Material 3** personalizado |
| **Injeção de Dependência** | **Hilt (Dagger)** |
| **Navegação** | **Navigation Compose** |
| **Animações e Pager** | **Compose Foundation (HorizontalPager, Canvas Sparklines)** |
| **Persistência Local & Sessão** | **Room Database** & **DataStore (Preferences)** |
| **Carregamento de Imagens** | **Coil 3** |
| **Concorrência** | **Kotlin Coroutines & StateFlow / Flow** |

### Network & Libs
| Categoria | Tecnologia |
| :--- | :--- |
| **API Client** | **Retrofit 2** & **OkHttp 4** |
| **Serialization** | **Kotlinx Serialization** |
| **Logs** | **HttpLoggingInterceptor** |

---

## Funcionalidades e Telas

### 🚀 Onboarding & Autenticação
* **Splash Screen:** Tela de abertura animada com animação suave de saída do logo da Águia Branca.
* **Onboarding Interativo:** Carrossel de 3 páginas (`HorizontalPager`) com imagens ilustrativas (`res/drawable`), indicadores de página (dots) e botões de ação `Começar →` e `Acessar`.
* **Tela de Login (`Acesse sua conta`):**
  * Cabeçalho corporativo com logo e tag `INOVAGAB`.
  * Form e-mail e senha com alternância de visibilidade (olho).
  * Atalho "Esqueci minha senha".
  * Link para "Criar conta" e selo "Seus dados estão protegidos".
  * Autenticação integrada diretamente com o backend Spring Boot via token JWT.
* **Tela de Cadastro (`Crie sua conta`):**
  * Campos para *Nome completo*, *E-mail*, *Empresa ou unidade*, *Senha*, *Confirmar senha* e opção expansível de *Código de acesso* (opcional).
  * Checkbox dos Termos de Uso e Política de Privacidade.
  * Integração com repositório para criação, atribuição de perfil via backend e autenticação automática do novo usuário.

---

### 👤 Perfil Operador (`João Silva` — `operador@aguia.com`)
* **Header de Operador:** TopBar com logo e tag exclusiva `OPERADOR`.
* **Home do Operador:**
  * Métricas em cards lado a lado: *Enviadas*, *Em análise* e *Aprovadas*.
  * Seções "Orientações estratégicas" e "Acompanhe suas sugestões".
  * Botão de ação principal `+ Nova sugestão`.
* **Nova Sugestão (Multi-step Wizard):**
  * Indicador de progresso em 3 etapas: `(1) Categoria` ➔ `(2) Detalhes` ➔ `(3) Revisão`.
  * Grid de 8 categorias: *Atendimento*, *Segurança*, *Operação*, *Manutenção*, *Tecnologia*, *Sustentabilidade*, *Pessoas* e *Outros*.
* **Minhas Sugestões:**
  * Carrossel de pílulas de filtro (*Todas*, *Enviadas*, *Em análise*, *Aprovadas*).
  * Cards detalhados indicando data, categoria e status.
* **Detalhes da Sugestão:**
  * Visualização da descrição completa, benefícios esperados e **Linha do tempo de Acompanhamento** (*Enviada*, *Recebida pelo gestor*, *Aguardando decisão*).
* **Mural de Comunicações:**
  * Comunicados e avisos internos organizados por cartões com ícones e categorias (*Inovação*, *Frota*, *Sustentabilidade*).
* **Perfil do Colaborador:**
  * Exibição de foto/avatar, nome, cargo (*Operador*), e-mail, opções de *Dados pessoais*, *Segurança* e botão de logout *Sair*.

---

### 💼 Perfil Gestor (`Mariana Costa` — `gestor@aguia.com`)
* **Header de Gestor:** TopBar com logo e tag exclusiva `GESTOR`.
* **Home do Gestor:**
  * Grid 2x2 com ícones circulares coloridos: *Recebidas (24)*, *Em análise (8)*, *Aprovadas (10)* e *Projetos (6)*.
  * Seção "Ideias para análise" com atalho "Ver todas" e botão de ação `Analisar ideias`.
* **Análise de Ideias:**
  * Campo de busca ("Buscar ideias...") com botão de filtro.
  * Carrossel de pílulas de status (*Todas*, *Enviadas*, *Em análise*, *Aprovadas*).
  * Visualização do autor da ideia (*Lucas Almeida*, *Juliana Martins*, *Rafael Souza*, *Beatriz Lima*) e indicador de prioridade (*Alta*, *Média*, *Baixa*).
* **Gestão de Projetos:**
  * Abas de navegação (*Meus projetos* | *Todos*).
  * Cards de projetos atrelados com barra de progresso (%), prazo, investimento e avatar com as iniciais do responsável (ex: `LA` para *Lucas Almeida*).
* **Perfil da Gestora:**
  * Layout corporativo adaptado (*Mariana Costa* — *Gestora de Inovação*).

---

### 📊 Perfil Liderança (`Carlos Mendes` — `lideranca@aguia.com`)
* **Header de Liderança:** TopBar com logo e tag exclusiva `LIDERANÇA`.
* **Dashboard Executivo:**
  * Grid de métricas 2x2 com **gráficos sparklines em verde desenhados em Canvas** para visualizar tendências:
    * **ROI Global:** `24,8%`
    * **Lucro Gerado:** `R$ 2,4 mi`
    * **Redução de Custos:** `R$ 1,1 mi`
    * **Produtividade:** `+32%`
  * Cards de Resumo Operacional (*Projetos em andamento* e *Ideias aprovadas*).
* **Diretrizes Estratégicas:**
  * Abas (*Publicadas* | *Rascunhos*) e campo de busca.
  * Ações de *Editar*, *Visualizar* e *Excluir* diretrizes.
  * Botão flutuante estendido `+ Nova diretriz`.
* **Gestão de Portfólio:**
  * Resumo com **Investimento Total (R$ 455 mi)** e **Total de Projetos (3)**.
* **Mensuração de Resultados:**
  * Exibição do ROI, Retorno Financeiro e Investimento por projeto organizado em 3 colunas métricas.
* **Perfil Executivo:**
  * Exibição completa do perfil (*Carlos Mendes* — *Diretor de Inovação*).

---

## Autenticação Backend (Spring Boot)

O aplicativo integra com o backend real Spring Boot via endpoints REST e autenticação JWT Bearer:

* **Endpoint de Login:** `POST /api/auth/login`
* **Endpoint de Cadastro:** `POST /api/auth/cadastro` (cria usuário com papel `OPERADOR`)
* **Endpoint de Perfil:** `GET /api/auth/me`
* **JWT Persistence:** Token armazenado no `DataStore` e enviado via `AuthInterceptor` em rotas protegidas (`Authorization: Bearer <token>`).

---

## Arquitetura

O projeto adota **Clean Architecture** aliada ao padrão **MVVM (Model-View-ViewModel)** com **Unidirectional Data Flow (UDF)**.

```text
UI (Jetpack Compose) ──► ViewModel (StateFlow) ──► Repository (Domain/Data) ──► Local / Remote Data
```

### Princípios Aplicados
* **Single Source of Truth (SSOT):** O estado da tela é mantido no ViewModel em um `StateFlow` único.
* **Componentização Reutilizável:** Design System centralizado para padronização gráfica.
* **Injeção de Dependências:** Hilt provê instâncias únicas de repositórios e gerenciadores de sessão (`SessionManager`).

---

## Estrutura do Projeto

```text
app/src/main/java/br/com/inovagabv2/
├── core/
│   ├── designsystem/             # Temas (AguiaColors, Typography) e Componentes (TopBar, BottomBar, Sparkline, Cards)
│   ├── navigation/               # Definção de rotas (Screen) e AguiaNavHost
│   ├── session/                  # SessionManager (DataStore)
│   └── di/                       # Módulos de Injeção do Hilt
│
├── data/
│   ├── local/                    # Configurações do Room Database
│   ├── repository/               # Implementação dos repositórios de dados (Auth, Idea, Project, Strategy, Dashboard)
│   └── model/                    # Modelos de transferência (DTOs)
│
├── domain/
│   ├── model/                    # Modelos de domínio (User, Idea, Project, Strategy, Role, Priority)
│   └── repository/               # Interfaces dos repositórios
│
└── presentation/                 # Camada de Apresentação (UI e ViewModels)
    ├── splash/                   # Tela de abertura
    ├── onboarding/               # Carrossel de Boas-vindas (3 etapas)
    ├── auth/                     # Login (Acesse sua conta) e Cadastro (Crie sua conta)
    ├── operator/                 # Funcionalidades de Operador (Home, Nova Sugestão, Minhas Sugestões, Detalhes, Comunicações)
    ├── manager/                  # Funcionalidades de Gestor (Home, Análise de Ideias, Projetos)
    ├── leadership/               # Funcionalidades de Liderança (Dashboard, Estratégias, Portfólio, Resultados)
    └── profile/                  # Perfil executivo/operacional e Logout
```

---

## Como Executar

### Requisitos
* **Android Studio** Ladybug (2024.2.1) ou superior.
* **JDK:** Java 17.
* **Android SDK:** API Level 34 ou superior.

### Configuração de Rede (Backend Spring Boot)
O aplicativo se conecta ao servidor backend na porta 8080:
* **Emulador Android:** Utiliza por padrão `http://10.0.2.2:8080/` (fallback automático se `API_BASE_URL` não for definida em `local.properties`).
* **Dispositivo Físico:** Configure o endereço IPv4 da sua máquina local no arquivo `local.properties`:
  ```properties
  API_BASE_URL=http://192.168.1.7:8080/
  ```
* **Requisitos da Rede Local:** O smartphone e o computador devem estar conectados na **mesma rede Wi-Fi/local**.
* **Validação de Conexão:** Antes de executar a aplicação, acesse no navegador do celular a URL do backend:
  `http://<IPv4_DO_COMPUTADOR>:8080/actuator/health` (deve retornar `{"status":"UP"}`).

### Passos
1. **Clonar o repositório:**
   ```bash
   git clone https://github.com/LucasFerdev/inovagab-android.git
   ```
2. **Abrir no Android Studio:**
   * Importe a pasta do projeto e aguarde a sincronização do Gradle (`Gradle Sync`).
3. **Configurar a API Base URL:**
   * Crie ou edite o arquivo `local.properties` na raiz do projeto com o IP da sua máquina.
4. **Executar o aplicativo:**
   * Selecione um emulador (API 30+) ou dispositivo físico com Depuração USB ativada e clique no botão **Run ('app')**.

---

## Boas Práticas e Padrões
* **Design System Customizado:** Definições das cores institucionais (`PrimaryBlue #1677E8`, `NavyDark #06162F`, `SuccessGreen #20A66A`) centralizadas.
* **Canvas Custom Graphics:** Desenhador de gráficos *Sparkline* usando Canvas Compose para indicar tendências financeiras.
* **Clean Code & Typings:** Nomenclaturas claras e alinhadas ao domínio de **Transporte Rodoviário de Passageiros**.

---

## Autor

**Lucas Fernando da Silva**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/lucasferdev/)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/LucasFerdev)
