package br.com.inovagabv2.presentation

import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.repository.DashboardRepositoryImpl
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.presentation.leadership.LeadershipProjectsViewModel
import br.com.inovagabv2.presentation.manager.projects.create.parseDeadlineIso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class Checkpoint41Test {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(InovaGabApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        Dispatchers.resetMain()
    }

    @Test
    fun `date deadline validation parses DD MM AAAA and AAAA-MM-DD and rejects free text`() {
        assertEquals("2026-12-31", parseDeadlineIso("31/12/2026"))
        assertEquals("2026-12-31", parseDeadlineIso("2026-12-31"))
        assertNull(parseDeadlineIso("2 meses"))
        assertNull(parseDeadlineIso("invalid-date"))
    }

    @Test
    fun `single project with 35 percent progress yields average progress 35 exactly`() = runTest {
        val fakeRepo = object : br.com.inovagabv2.domain.repository.ProjectRepository {
            override fun getProjects() = flowOf(
                listOf(
                    Project(
                        id = "p1",
                        name = "Project 1",
                        description = "Desc",
                        percentualProgresso = 35
                    )
                )
            )
            override fun getProjectsRemote(status: ProjectStatus?, etapa: ProjectStage?, estrategiaId: String?, gestorId: String?, prazo: String?, pagina: Int, tamanho: Int) = flowOf(Pagina<Project>(emptyList(), 0, 20, 0, 0, true, true))
            override fun getProjectById(id: String) = flowOf(null)
            override suspend fun createProject(nome: String, descricao: String, estrategiaId: String, ideiaOrigemId: String?, investimento: BigDecimal, prazo: String) = Result.failure<Project>(Exception())
            override suspend fun createProject(project: Project) = Result.success(Unit)
            override suspend fun updateProject(id: String, nome: String, descricao: String, estrategiaId: String, ideiaOrigemId: String?, investimento: BigDecimal, prazo: String) = Result.failure<Project>(Exception())
            override suspend fun updateProject(project: Project) = Result.success(Unit)
            override suspend fun updateProgress(id: String, etapa: ProjectStage, status: ProjectStatus, percentualProgresso: Int, justificativa: String?) = Result.failure<Project>(Exception())
            override suspend fun registerResults(id: String, retornoFinanceiro: BigDecimal, ganhoProdutividadePercentual: BigDecimal, resultado: String) = Result.failure<Project>(Exception())
            override suspend fun concludeProject(id: String) = Result.failure<Project>(Exception())
            override suspend fun cancelProject(id: String) = Result.success(Unit)
        }

        val fakeAuth = object : br.com.inovagabv2.domain.repository.AuthRepository {
            override suspend fun login(email: String, password: String) = Result.failure<br.com.inovagabv2.domain.model.User>(Exception())
            override suspend fun register(nome: String, email: String, senha: String, empresa: String, codigoAcesso: String?) = Result.failure<br.com.inovagabv2.domain.model.User>(Exception())
            override suspend fun logout() {}
            override fun getCurrentUser() = flowOf(null)
        }

        val viewModel = LeadershipProjectsViewModel(fakeRepo, fakeAuth)
        val average = viewModel.averageProgress.first()

        assertEquals(35, average)
    }

    @Test
    fun `dashboard HTTP 500 error throws exception and does not hide error as zeroed data`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val repo = DashboardRepositoryImpl(api, json)

        var exceptionThrown = false
        try {
            repo.getDashboardData().first()
        } catch (e: Exception) {
            exceptionThrown = true
            assertTrue(e.message?.contains("Serviço do Dashboard indisponível") == true)
        }

        assertTrue(exceptionThrown)
    }
}
