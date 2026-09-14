package br.com.inovagabv2.presentation.manager.home

import br.com.inovagabv2.domain.model.*
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerHomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeIdeaRepository: FakeIdeaRepository
    private lateinit var fakeProjectRepository: FakeProjectRepository
    private lateinit var viewModel: ManagerHomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        fakeIdeaRepository = FakeIdeaRepository()
        fakeProjectRepository = FakeProjectRepository()
        viewModel = ManagerHomeViewModel(
            fakeAuthRepository,
            fakeIdeaRepository,
            fakeProjectRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty repositories produce zero metrics and empty ideas list`() = runTest {
        val currentState = viewModel.state.first { !it.isLoading }

        assertEquals(0, currentState.metrics.receivedIdeas)
        assertEquals(0, currentState.metrics.inAnalysisIdeas)
        assertEquals(0, currentState.metrics.approvedIdeas)
        assertEquals(0, currentState.metrics.activeProjects)
        assertTrue(currentState.ideas.isEmpty())
    }

    @Test
    fun `metrics are calculated dynamically from repositories`() = runTest {
        val ideas = listOf(
            Idea("1", "Idea 1", "Prob", "Sol", "Ben", "Operação", "s1", "u1", "Author", IdeaStatus.ENVIADA, 2),
            Idea("2", "Idea 2", "Prob", "Sol", "Ben", "Manutenção", "s1", "u2", "Author", IdeaStatus.EM_ANALISE, 4),
            Idea("3", "Idea 3", "Prob", "Sol", "Ben", "Tecnologia", "s1", "u3", "Author", IdeaStatus.APROVADA, 5)
        )
        fakeIdeaRepository.setIdeas(ideas)

        val projects = listOf(
            Project("p1", "Project 1", "Desc", "s1", null, ProjectStage.DESENVOLVIMENTO, ProjectStatus.EM_ANDAMENTO, 50, BigDecimal("100000.00"), "2026-12-31")
        )
        fakeProjectRepository.setProjects(projects)

        val currentState = viewModel.state.first { !it.isLoading && it.ideas.isNotEmpty() }

        assertEquals(3, currentState.metrics.receivedIdeas)
        assertEquals(1, currentState.metrics.inAnalysisIdeas)
        assertEquals(1, currentState.metrics.approvedIdeas)
        assertEquals(1, currentState.metrics.activeProjects)
        assertEquals(2, currentState.ideas.size) // ENVIADA & EM_ANALISE
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String) = Result.success(User("m1", "Mariana Costa", email, Role.GESTOR))
        override suspend fun register(nome: String, email: String, senha: String, empresa: String, codigoAcesso: String?) = Result.success(User("m1", nome, email, Role.GESTOR))
        override suspend fun logout() {}
        override fun getCurrentUser(): Flow<User?> = flowOf(User("m1", "Mariana Costa", "gestor@aguia.com", Role.GESTOR))
    }

    private class FakeIdeaRepository : IdeaRepository {
        private val _flow = MutableStateFlow<List<Idea>>(emptyList())
        fun setIdeas(list: List<Idea>) { _flow.value = list }
        override fun getIdeas(): Flow<List<Idea>> = _flow
        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = _flow
        override fun getIdeasRemote(status: IdeaStatus?, categoria: String?, estrategiaId: String?, prioridade: Int?, pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(_flow.value, 0, 20, _flow.value.size.toLong(), 1, true, true))
        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(_flow.value, 0, 20, _flow.value.size.toLong(), 1, true, true))
        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(null)
        override suspend fun createIdea(titulo: String, problema: String, solucaoProposta: String, beneficiosEsperados: String, categoria: String, estrategiaId: String): Result<Idea> = Result.success(Idea("1", titulo, problema, solucaoProposta, beneficiosEsperados, categoria, estrategiaId))
        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)
        override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Int?, justificativa: String?): Result<Unit> = Result.success(Unit)
        override suspend fun analisar(id: String): Result<Idea> = Result.success(Idea("1", "T", "P", "S", "B", "C", "E"))
        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> = Result.success(Idea("1", "T", "P", "S", "B", "C", "E"))
        override suspend fun aprovar(id: String): Result<Idea> = Result.success(Idea("1", "T", "P", "S", "B", "C", "E"))
        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> = Result.success(Idea("1", "T", "P", "S", "B", "C", "E"))
        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeProjectRepository : ProjectRepository {
        private val _flow = MutableStateFlow<List<Project>>(emptyList())
        fun setProjects(list: List<Project>) { _flow.value = list }
        override fun getProjects(): Flow<List<Project>> = _flow
        override fun getProjectsRemote(status: ProjectStatus?, etapa: ProjectStage?, estrategiaId: String?, gestorId: String?, prazo: String?, pagina: Int, tamanho: Int): Flow<Pagina<Project>> = flowOf(Pagina(_flow.value, 0, 20, _flow.value.size.toLong(), 1, true, true))
        override fun getProjectById(id: String): Flow<Project?> = flowOf(null)
        override suspend fun createProject(nome: String, descricao: String, estrategiaId: String, ideiaOrigemId: String?, investimento: BigDecimal, prazo: String): Result<Project> = Result.success(Project("p1", nome, descricao))
        override suspend fun createProject(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun updateProject(id: String, nome: String, descricao: String, estrategiaId: String, ideiaOrigemId: String?, investimento: BigDecimal, prazo: String): Result<Project> = Result.success(Project("p1", nome, descricao))
        override suspend fun updateProject(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun updateProgress(id: String, etapa: ProjectStage, status: ProjectStatus, percentualProgresso: Int, justificativa: String?): Result<Project> = Result.success(Project("p1", "P", "D"))
        override suspend fun registerResults(id: String, retornoFinanceiro: BigDecimal, ganhoProdutividadePercentual: BigDecimal, resultado: String): Result<Project> = Result.success(Project("p1", "P", "D"))
        override suspend fun concludeProject(id: String): Result<Project> = Result.success(Project("p1", "P", "D"))
        override suspend fun cancelProject(id: String): Result<Unit> = Result.success(Unit)
    }
}
