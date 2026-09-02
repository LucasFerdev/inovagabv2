# InovaGAB — Gestão de Inovação Águia Branca
Plataforma corporativa móvel para gestão de sugestões, diretrizes estratégicas e indicadores de impacto, focada no setor de transporte rodoviário de passageiros da **Viação Águia Branca**.

Projeto focado em produtividade e inovação interna — Desenvolvimento Android Nativo.

## Sumário
* [Sobre o projeto](#sobre-o-projeto)
* [Tecnologias](#tecnologias)
* [Funcionalidades](#funcionalidades)
* [Arquitetura](#arquitetura)
* [Estrutura de pastas](#estrutura-de-pastas)
* [Como executar](#como-executar)
* [Perfis de Acesso](#perfis-de-acesso)
* [Boas práticas](#boas-práticas)
* [Autor](#autor)

---

## Sobre o projeto
O **InovaGAB** é um ecossistema mobile unificado que conecta a operação à liderança da empresa. O objetivo é democratizar a inovação, permitindo que colaboradores da ponta enviem sugestões de melhoria enquanto gestores e diretores acompanham resultados financeiros e operacionais em tempo real.

O projeto simula o fluxo real de uma empresa de grande porte:
1. **Operador:** Consulta diretrizes, registra problemas/ideias e acompanha o status via timeline.
2. **Gestor:** Analisa as sugestões da equipe, define prioridades e gerencia projetos ativos.
3. **Liderança:** Visão executiva de alto nível com métricas de ROI, lucro e redução de custos.

---

## Tecnologias

### Mobile Stack
| Categoria | Tecnologia |
| :--- | :--- |
| Linguagem | **Kotlin** |
| Interface | **Jetpack Compose** |
| Design System | **Material 3** |
| Injeção de Dependência | **Hilt (Dagger)** |
| Navegação | **Navigation Compose** |
| Persistência Local | **Room Database** |
| Sessão | **DataStore (Preferences)** |
| Imagens | **Coil 3** |
| Async/Streams | **Coroutines & Flow** |

### Network & Data
| Categoria | Tecnologia |
| :--- | :--- |
| API Client | **Retrofit 2** |
| HTTP Client | **OkHttp 4** |
| Serialization | **Kotlinx Serialization** |
| Logs | **HttpLoggingInterceptor** |

---

## Funcionalidades

### Autenticação e Abertura
| Funcionalidade | Descrição |
| :--- | :--- |
| **Splash Screen** | Tela de abertura animada com o logo da Viação Águia Branca deslizando para a esquerda sobre fundo branco. |
| **Login / Sessão** | Autenticação por perfis com persistência de sessão via DataStore. |

### Experiência do Operador
| Funcionalidade | Descrição |
| :--- | :--- |
| **Dashboard Home** | Resumo de contribuições e acesso rápido às diretrizes estratégicas. |
| **Nova Sugestão** | Fluxo multi-step (3 etapas): Categoria → Detalhes → Revisão. |
| **Timeline de Status** | Acompanhamento visual da evolução da ideia (Enviada, Em análise, Aprovada, etc). |
| **Comunicações** | Mural de avisos e comunicados internos da Viação Águia Branca. |

### Gestão e Estratégia
| Funcionalidade | Descrição |
| :--- | :--- |
| **Análise de Ideias** | Filtros por status e definição de prioridade (Baixa, Média, Alta) pelo Gestor. |
| **Gestão de Projetos** | Acompanhamento de progresso (%) e indicadores de projetos em andamento. |
| **Dashboard Executivo** | Visualização de ROI Global, Lucro Gerado e Ganho de Produtividade. |
| **Gestão de Diretrizes** | CRUD completo de estratégias e publicação de metas para a liderança. |

---

## Arquitetura
O projeto utiliza **Clean Architecture** combinada com o padrão **MVVM (Model-View-ViewModel)** para garantir separação de responsabilidades, facilidade de manutenção e testabilidade.

```text
UI (Compose) ───► ViewModel ───► Use Case (Domain) ───► Repository ───► Data Source (Local/Remote)
```

### Comunicação
* **Unidirectional Data Flow (UDF):** O estado flui para baixo e os eventos fluem para cima.
* **UI State:** As telas observam um `StateFlow` único que representa o estado completo da interface (Loading, Success, Empty, Error).
* **Dependency Injection:** Hilt gerencia o ciclo de vida das dependências e o provimento de repositórios.

---

## Estrutura de pastas
```text
app/src/main/java/br/com/inovagabv2/
├── core/
│   ├── designsystem/     # AguiaTheme, Colors, Typography e Componentes reutilizáveis
│   ├── navigation/       # Definições de rotas (Screen) e AguiaNavHost
│   ├── session/          # SessionManager para persistência via DataStore
│   └── di/               # Módulos do Hilt para injeção de dependência
│
├── data/
│   ├── local/            # Configurações do Room (DAOs e Database)
│   ├── repository/       # Implementações dos repositórios (atualmente via MockData)
│   └── model/            # Data Transfer Objects (DTOs)
│
├── domain/
│   ├── model/            # Modelos de negócio puros (Idea, Project, Strategy, User)
│   └── repository/       # Interfaces de contrato dos repositórios
│
└── presentation/         # Camada de interface organizada por Feature
    ├── auth/             # Fluxo de Login
    ├── operator/         # Funcionalidades de Operador (Home, Sugestões, Diretrizes)
    ├── manager/          # Funcionalidades de Gestor (Análise e Projetos)
    ├── leadership/       # Funcionalidades de Liderança (Dashboard e Estratégias)
    └── profile/          # Perfil do colaborador com Foto e Logout
```

---

## Como executar

### Requisitos
* Android Studio Ladybug (ou superior).
* Java JDK 17.
* Android SDK 34 ou superior.

### Passos
1. **Clonar o repositório**
   ```bash
   git clone https://github.com/LucasFerdev/inovagab-android.git
   ```
2. **Importar no Android Studio**
   * Aguarde o `Gradle Sync` concluir todas as dependências.
3. **Executar**
   * Selecione um emulador (API 30+) ou dispositivo físico e clique em `Run 'app'`.

---

## Perfis de Acesso
O aplicativo ajusta a navegação e funcionalidades automaticamente com base no e-mail de login (dados mockados):

* **Operador:** `operador@aguia.com`
* **Gestor:** `gestor@aguia.com`
* **Liderança:** `lideranca@aguia.com`
* *Senha: Qualquer valor (Simulado)*

---

## Boas práticas
* **Responsividade:** Uso de `Scaffold` e layouts adaptáveis para suporte a diferentes tamanhos de tela.
* **Componentização:** Design System centralizado (`AguiaTopBar`, `AguiaBottomBar`, `AguiaCard`) evitando duplicidade de código.
* **Modern Android:** Uso de `Photo Picker` via `ActivityResultContracts` para seleção de foto de perfil.
* **Clean Context:** Toda a terminologia e dados de exemplo foram adaptados para o contexto real de **Transporte Rodoviário**.

---

## Autor
**Lucas Fernando da Silva**

[LinkedIn](https://www.linkedin.com/in/lucasferdev/) | [GitHub](https://github.com/LucasFerdev)
