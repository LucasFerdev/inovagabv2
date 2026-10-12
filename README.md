# InovaGAB — Gestão de Inovação Águia Branca (Android)

Plataforma corporativa móvel nativa Android para gestão de ideias, diretrizes estratégicas, projetos de inovação e análise consultiva com Inteligência Artificial, desenvolvida para a **Viação Águia Branca**.

---

## 📋 Sumário
- [Visão Geral](#-visão-geral)
- [Funcionalidades por Perfil](#-funcionalidades-por-perfil)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Clonagem e Abertura do Projeto](#-clonagem-e-abertura-do-projeto)
- [Configuração da API (Backend)](#-configuração-da-api-backend)
- [Compilação e Execução](#-execução)
- [Cadastro e Autenticação](#-cadastro-e-autenticação)
- [Fluxo de Demonstração Recomendado](#-fluxo-de-demonstração-recomendado)
- [Arquitetura](#-arquitetura)
- [Estados das Telas](#-estados-das-telas)
- [Inteligência Artificial (IA)](#-inteligência-artificial-ia)
- [Testes Automatizados](#-testes)
- [Solução de Problemas](#-solução-de-problemas)
- [Segurança](#-segurança)
- [Estado do Projeto](#-estado-do-projeto)

---

## 🎯 Visão Geral

O **InovaGAB** é uma aplicação Android nativa desenvolvida para conectar colaboradores operacionais, gestores e a liderança executiva da Viação Águia Branca em um fluxo unificado de inovação corporativa.

O aplicativo integra-se diretamente via HTTP REST/JSON com a **InovaGAB API (Spring Boot)**. Toda a persistência de dados, regras de negócio, cálculo de ROI, consolidação de indicadores e comunicação com a API do Google Gemini são gerenciadas exclusivamente pelo backend.

O sistema atende a três perfis de acesso bem definidos:
- **OPERADOR:** Colaborador da linha de frente que sugere ideias e acompanha seu progresso.
- **GESTOR:** Responsável por analisar, priorizar, aprovar/rejeitar ideias, consultar a IA e administrar o portfólio de projetos ativos.
- **LIDERANÇA:** Diretoria responsável por definir as estratégias corporativas e acompanhar os dashboards executivos de resultados.

---

## ⚙️ Funcionalidades por Perfil

### 👷 OPERADOR
- **Direcionamentos Estratégicos:** Consulta a lista de estratégias ativas publicadas pela liderança.
- **Submissão de Ideias:** Cadastra novas ideias selecionando categoria, descrição detalhada do problema, solução proposta, benefícios esperados e estratégia vinculada.
- **Acompanhamento de Ideias:** Acessa *"Minhas Ideias"*, filtra por status (*Enviadas*, *Em análise*, *Aprovadas*, *Rejeitadas*) e consulta detalhes e histórico de alterações em tempo real.
- **Ranking de Inovação:** Visualiza a classificação geral e destaques dos colaboradores com ideias transformadas em projetos concluídos.
- **Perfil e Suporte:** Acessa os dados da conta ativa e dispara solicitação de suporte via aplicativo de e-mail.

### 👔 GESTOR
- **Avaliação de Ideias:** Filtra ideias enviadas e em análise, define a prioridade de P1 a P5, e executa ações de aprovação ou rejeição (com justificativa obrigatória).
- **Análise Consultiva da IA:** Solicita e reavalia análises automáticas geradas pela Inteligência Artificial contendo pontuação (0–100), prioridade sugerida, pontos fortes, riscos e recomendações.
- **Administração de Projetos:** Cria projetos vinculados a estratégias ativas e ideias aprovadas. Atualiza etapa, status e percentual de progresso (com justificativa em caso de redução).
- **Registro de Resultados:** Registra retorno financeiro obtido, ganho de produtividade (%) e resumo de resultados. Executa conclusão ou cancelamento de projetos.
- **Consultas e Ranking:** Consulta diretrizes estratégicas, ranking de inovação, histórico de alterações de cada item e perfil de acesso.

### 📊 LIDERANÇA
- **Administração de Estratégias:** Cria, edita, ativa, desativa e arquiva diretrizes estratégicas corporativas.
- **Dashboard Executivo:** Visualiza métricas consolidadas pelo backend, incluindo Investimento Total, Retorno Financeiro, Lucro Gerado, ROI Global %, Produtividade Média, Projetos em Atraso e distribuição de projetos por status.
- **Dashboard por Estratégia e Projeto:** Consulta indicadores detalhados e gráficos consolidados por diretriz estratégica e por projeto individual.
- **Consultas Globais:** Consulta portfólio de ideias, análises da IA, histórico de alterações, ranking de inovação e perfil executivo.

---

## 🛠️ Tecnologias Utilizadas

As seguintes tecnologias estão integradas no projeto Android:

| Categoria | Tecnologia |
| :--- | :--- |
| **Linguagem** | **Kotlin 100%** (JVM Target 17) |
| **Interface do Usuário** | **Jetpack Compose** & **Material 3** |
| **Arquitetura** | **MVVM (Model-View-ViewModel)** & **Unidirectional Data Flow** |
| **Injeção de Dependência** | **Hilt (Dagger)** |
| **Comunicação de Rede** | **Retrofit 2** & **OkHttp 4** (HttpLoggingInterceptor) |
| **Serialização JSON** | **Kotlinx Serialization** |
| **Sessão Local** | **DataStore Preferences** |
| **Navegação** | **Navigation Compose** |
| **Concorrência** | **Kotlin Coroutines** & **StateFlow / Flow** |
| **Testes Automatizados** | **JUnit 4** & **MockWebServer** |

*(Nota: O aplicativo não utiliza persitência estática offline local ou bibliotecas não declaradas no Gradle).*

---

## 💻 Pré-requisitos

Para compilar e executar o aplicativo, certifique-se de possuir:
1. **Android Studio:** Versão Ladybug (2024.2.1) ou superior.
2. **JDK:** Java 17 (exigido pelas configurações do Gradle).
3. **Android SDK:** API Level 34 ou superior (compileSdk 37, minSdk 26).
4. **Dispositivo/Emulador:** Aparelho físico com depuração USB ativada ou emulador Android Studio (API 26+).
5. **InovaGAB API:** Servidor Spring Boot da aplicação em execução na rede local (porta `8080`).
6. **Git:** Ferramenta de controle de versão instalada.

---

## 📥 Clonagem e Abertura do Projeto

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/LucasFerdev/inovagab-android.git
   ```

2. **Abra o projeto no Android Studio:**
   - Selecione a pasta raiz `inovagabv2-main`.
   - Aguarde o término do **Gradle Sync**.
   - Verifique na barra superior se o módulo escolhido para execução é o `:app`.

---

## 🌐 Configuração da API (Backend)

O aplicativo Android lê o endereço do servidor backend a partir do arquivo `local.properties` situado na raiz do projeto Android.

### 1. Editar ou Criar o Arquivo `local.properties`
Crie ou edite o arquivo `local.properties` na raiz do projeto Android e adicione a propriedade `API_BASE_URL`:

- **Caso esteja utilizando o Emulador Android Studio:**
  ```properties
  API_BASE_URL=http://10.0.2.2:8080/
  ```

- **Caso esteja utilizando um Dispositivo Físico (Celular via Wi-Fi):**
  ```properties
  API_BASE_URL=http://192.168.X.X:8080/
  ```
  *(Substitua `192.168.X.X` pelo endereço IPv4 atual do computador onde o backend está rodando).*

> [!NOTE]
> - O script `build.gradle.kts` ajusta automaticamente a barra `/` final caso seja omitida.
> - O arquivo `local.properties` está no `.gitignore` e **nunca** deve ser commitado no repositório.

### 2. Descobrir o IP do Computador na Rede Local
- **Windows:** Abra o Terminal/Prompt de Comando e digite `ipconfig`. Procure pelo *Endereço IPv4* do seu adaptador Wi-Fi ou Ethernet (ex: `192.168.1.15`).
- **Linux/macOS:** Abra o Terminal e digite `ifconfig` ou `ip a`.

### 3. Validação de Conexão na Rede
1. Certifique-se de que o computador e o celular físico estão conectados na **mesma rede Wi-Fi**.
2. Abra o navegador do celular e acesse:
   ```text
   http://<IP_DO_SEU_COMPUTADOR>:8080/actuator/health
   ```
   Se a conexão estiver correta, a página retornará `{"status":"UP"}`.
3. Caso a conexão falhe, verifique se a porta `8080` está liberada no Firewall do Windows/OS.

---

## 🚀 Compilação e Execução

1. **Inicie o Backend:** Garanta que a aplicação Spring Boot do InovaGAB esteja em execução.
2. **Execute o Gradle Sync:** No Android Studio, clique no ícone do elefante (*Sync Project with Gradle Files*).
3. **Execute o Módulo `:app`:** Selecione o emulador ou dispositivo conectado e clique no botão **Run** (`Shift + F10`).

### Comandos de Linha de Comando (CLI)

- **Executar Testes Unitários:**
  - Windows: `gradlew.bat testDebugUnitTest`
  - Unix/macOS: `./gradlew testDebugUnitTest`

- **Gerar APK de Debug:**
  - Windows: `gradlew.bat assembleDebug`
  - Unix/macOS: `./gradlew assembleDebug`

> O APK compilado será gerado no diretório:
> `app/build/outputs/apk/debug/app-debug.apk`

---

## 🔐 Cadastro e Autenticação

1. **Cadastro Padrão:** Qualquer novo cadastro efetuado na tela de registro cria automaticamente um usuário com o papel **OPERADOR**.
2. **Cadastro com Código de Acesso Especial:** Caso seja fornecido um código de acesso válido configurado no backend, a conta é criada como **GESTOR** ou **LIDERANÇA**. Os Codigos estão disponiveis no documento de apresentação InovaGAB pagina 27 Perfil de Gestor. Consta Codigo de gestor e liderança
3. **Persistência do JWT:** O token JWT de autenticação retornado no login é armazenado com segurança no `DataStore Preferences` e injetado via `AuthInterceptor` no cabeçalho HTTP (`Authorization: Bearer <token>`).
4. **Sessão Expirada (HTTP 401):** Quando a sessão expira, o aplicativo captura o erro 401, limpa o token mantendo o estado de onboarding concluído e orienta o usuário a refazer o login com segurança, sem fechamento do app.

---

## 🔄 Fluxo de Demonstração Recomendado

Para testar o fluxo integrado completo de ponta a ponta:

1. **Visão da Liderança:**
   - Faça login como Liderança ou crie uma conta com o código de acesso correspondente.
   - Crie e ative uma nova Diretriz Estratégica (ex: *"Eficiência Energética na Frota"*).

2. **Visão do Operador:**
   - Faça login com um perfil Operador.
   - Acesse a aba de Estratégias e visualize a nova diretriz publicada.
   - Cadastre uma nova ideia vinculada a essa estratégia.
   - Acompanhe o status da ideia na tela *"Minhas Ideias"*.

3. **Visão do Gestor:**
   - Faça login com um perfil Gestor.
   - Acesse a tela de Avaliação de Ideias e visualize a proposta enviada.
   - Solicite a análise consultiva da Inteligência Artificial.
   - Defina a prioridade da ideia (P1 a P5) e aprove a proposta.
   - Acesse a aba de Projetos e crie um novo projeto vinculado a essa ideia aprovada.
   - Atualize a etapa, o progresso e registre os resultados financeiros/operacionais obtidos.
   - Conclua o projeto.

4. **Consolidação na Liderança e Ranking:**
   - Retorne com o perfil da Liderança e acesse o Dashboard Executivo para visualizar os indicadores atualizados de ROI e lucro gerado.
   - Acesse a tela de **Ranking de Inovação** no Perfil de qualquer usuário para visualizar o operador classificado no pódio por ter sua ideia transformada em projeto concluído.

---

## 📐 Arquitetura

O projeto adota os princípios da **Clean Architecture** e do padrão **MVVM**:

```text
app/src/main/java/br/com/inovagabv2/
├── core/
│   ├── designsystem/             # Design System (Cores, Tipografia e Componentes Reutilizáveis)
│   ├── di/                       # Módulos de Injeção de Dependências (Hilt)
│   ├── navigation/               # Mapeamento de Rotas (Screen) e NavHost
│   ├── session/                  # Gerenciador de Sessão (DataStore)
│   └── util/                     # Utilitários de formatação e máscaras
│
├── data/
│   ├── mapper/                   # Mapeadores de DTOs para Modelos de Domínio
│   ├── remote/                   # Interface Retrofit (InovaGabApi), Interceptadores e DTOs
│   └── repository/               # Implementação dos Repositórios de Dados
│
├── domain/
│   ├── model/                    # Modelos de Domínio (User, Idea, Project, Strategy, Ranking, etc.)
│   └── repository/               # Interfaces dos Repositórios
│
└── presentation/                 # Camadas de Interface (Compose Views) e ViewModels
    ├── auth/                     # Autenticação (Login e Cadastro)
    ├── leadership/               # Telas da Liderança (Dashboard, Estratégias, Detalhes)
    ├── manager/                  # Telas do Gestor (Avaliação, Projetos, Criação)
    ├── onboarding/               # Onboarding de boas-vindas
    ├── operator/                 # Telas do Operador (Home, Ideias, Estratégias, Detalhes)
    ├── profile/                  # Perfil do Usuário e Suporte por E-mail
    ├── ranking/                  # Ranking de Inovação
    └── splash/                   # Tela de Abertura (Splash)
```

- **Data DTO & Mapper:** Isola o contrato de rede da lógica interna do aplicativo.
- **Repository Pattern:** Centraliza as chamadas de rede e expõe reatividade via `Flow`.
- **ViewModel & StateFlow:** Gerencia o estado da interface em um `StateFlow` único reativo (UDF).
- **Compose Screens:** Telas declarativas desacopladas de lógica de negócios.

---

## 📊 Estados das Telas

Todas as telas que consomem dados do backend possuem tratamento reativo para 4 estados principais:
- **Loading:** Indicadores visuais de carregamento (*AguiaLoadingState* / *CircularProgressIndicator*).
- **Success:** Exibição dos dados reais retornados pela API.
- **Empty:** Mensagem amigável contextual quando a consulta retorna uma lista vazia do servidor.
- **Error:** Mensagem de erro informativa com botão *"Tentar novamente"* em caso de falha de conexão ou erro HTTP.

---

## 🤖 Inteligência Artificial (IA)

- O aplicativo Android **não** possui integração direta nem armazena chaves de API do Google Gemini.
- A requisição de análise é enviada para os endpoints protegidos do backend (`/api/ia/...`).
- O servidor backend realiza a chamada segura à API da IA, calcula os indicadores e retorna a resposta formatada para o aplicativo.
- A análise gerada pela IA possui caráter **estritamente consultivo**, permanecendo a decisão de aprovação ou rejeição sob responsabilidade do Gestor.

---

## 🧪 Testes Automatizados

Para executar a suíte de testes unitários:
```bash
gradlew.bat testDebugUnitTest
```

- Os repositórios de dados são testados utilizando **MockWebServer** para validar a desserialização de JSONs e o tratamento de códigos de erro HTTP (400, 401, 403, 404, 500).
- Os ViewModels e utilitários são cobertos por testes unitários com test dispatchers de Coroutines.

---

## 🛠️ Solução de Problemas

| Problema | Causa Provável | Solução |
| :--- | :--- | :--- |
| **Botão Run desativado no Android Studio** | Módulo do projeto não selecionado ou Gradle Sync pendente. | Execute *Sync Project with Gradle Files* e selecione o módulo `:app` na barra superior. |
| **Erro de conexão ao tentar logar ou carregar dados** | Backend Spring Boot desligado ou IP incorreto em `local.properties`. | Certifique-se de que o backend está rodando e valide o IP na URL `http://<IP>:8080/actuator/health`. |
| **Dispositivo físico não conecta ao servidor** | Aparelho e computador em redes Wi-Fi diferentes ou porta bloqueada. | Conecte ambos na mesma rede e libere a porta `8080` no Firewall. |
| **Sessão Expirada / Erro 401** | Token JWT expirou ou foi invalidado no servidor. | O app limpa o token automaticamente. Refaça o login na aplicação. |
| **Corpo da requisição inválido na criação de projeto** | Data formatada incorretamente no formulário. | Informe uma data válida no formato `DD/MM/AAAA` (ex: `31/12/2026`). O app converterá para `AAAA-MM-DD`. |
| **Falha na Análise da IA** | Chave da IA não configurada no backend ou indisponibilidade temporária. | Verifique se a variável de ambiente do Gemini está configurada no servidor Spring Boot. |

---

## 🔒 Segurança

- O arquivo `local.properties` está contido no `.gitignore` e não é enviado ao repositório.
- Nenhuma chave privada, secret ou chave de API do Gemini é armazenada no código-fonte do Android.
- As comunicações protegidas exigem o cabeçalho HTTP `Authorization: Bearer <token>`.

---

## 📌 Estado do Projeto

Este projeto consiste em um **MVP acadêmico/demonstrativo** de alta fidelidade desenvolvido como parte de projeto integrador.
- **Limitações Atuais:** Não há suporte a modo offline prolongado sem conexão com o servidor nem sincronização em segundo plano por Push Notifications.

---

## 👤 Autor

**Lucas Fernando da Silva**
Desenvolvedor Android Nativo
