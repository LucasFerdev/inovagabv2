package br.com.inovagabv2.presentation.manager.projects

import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerProjectsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh busca projetos de novo`() = runTest {
        val repository = FakeProjectRepository()
        val viewModel = ManagerProjectsViewModel(repository, FakeAuthRepository())

        assertEquals(1, repository.calls)
        assertEquals("p1", viewModel.state.value.projects.single().id)
        assertNull(viewModel.state.value.error)

        viewModel.refresh()

        assertEquals(2, repository.calls)
        assertEquals("p2", viewModel.state.value.projects.single().id)
        assertEquals(0, viewModel.state.value.selectedTab)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `trocar de aba cancela a carga anterior`() = runTest {
        val repository = FakeProjectRepository(suspendFirstCall = true)
        val viewModel = ManagerProjectsViewModel(repository, FakeAuthRepository())

        assertEquals(1, repository.calls)
        assertEquals(true, viewModel.state.value.isLoading)

        viewModel.onTabSelected(1)

        assertEquals(2, repository.calls)
        assertEquals(1, viewModel.state.value.selectedTab)
        assertEquals("todos", viewModel.state.value.projects.single().id)
        assertNull(repository.lastGestorId)
        assertNull(viewModel.state.value.error)

        repository.releaseFirstCall()

        assertFalse(repository.emittedStaleResult)
        assertEquals("todos", viewModel.state.value.projects.single().id)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String) =
            Result.success(User("g1", "Gestor", email, Role.GESTOR))

        override suspend fun register(
            nome: String,
            email: String,
            senha: String,
            empresa: String,
            codigoAcesso: String?
        ) = Result.success(User("g1", nome, email, Role.GESTOR))

        override suspend fun logout() {}
        override fun getCurrentUser(): Flow<User?> = flowOf(User("g1", "Gestor", "gestor@aguia.com", Role.GESTOR))
    }

    private class FakeProjectRepository(
        private val suspendFirstCall: Boolean = false
    ) : ProjectRepository {
        var calls = 0
        var lastGestorId: String? = "not-called"
        var emittedStaleResult = false
        private val firstCallGate = CompletableDeferred<Unit>()

        fun releaseFirstCall() {
            firstCallGate.complete(Unit)
        }

        override fun getProjects(): Flow<List<Project>> = flowOf(emptyList())

        override fun getProjectsRemote(
            status: ProjectStatus?,
            etapa: ProjectStage?,
            estrategiaId: String?,
            gestorId: String?,
            prazo: String?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Project>> = flow {
            calls++
            val call = calls
            lastGestorId = gestorId
            if (suspendFirstCall && call == 1) {
                firstCallGate.await()
                emittedStaleResult = true
                emit(page(listOf(Project("stale", "Atrasado", "Desc"))))
            } else {
                val id = if (gestorId == null) "todos" else "p$call"
                emit(page(listOf(Project(id, "Projeto $call", "Desc"))))
            }
        }

        override fun getProjectById(id: String): Flow<Project?> = flowOf(null)
        override fun consultarHistorico(id: String): Flow<List<HistoryItem>> = flowOf(emptyList())

        override suspend fun createProject(
            nome: String,
            descricao: String,
            estrategiaId: String,
            ideiaOrigemId: String?,
            investimento: BigDecimal,
            prazo: String
        ): Result<Project> = Result.success(Project("p1", nome, descricao))

        override suspend fun createProject(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun updateProject(
            id: String,
            nome: String,
            descricao: String,
            estrategiaId: String,
            ideiaOrigemId: String?,
            investimento: BigDecimal,
            prazo: String
        ): Result<Project> = Result.success(Project(id, nome, descricao))

        override suspend fun updateProject(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun updateProgress(
            id: String,
            etapa: ProjectStage,
            status: ProjectStatus,
            percentualProgresso: Int,
            justificativa: String?
        ): Result<Project> = Result.success(Project("p1", "P", "D"))

        override suspend fun registerResults(
            id: String,
            retornoFinanceiro: BigDecimal,
            ganhoProdutividadePercentual: BigDecimal,
            resultado: String
        ): Result<Project> = Result.success(Project("p1", "P", "D"))

        override suspend fun concludeProject(id: String): Result<Project> = Result.success(Project("p1", "P", "D"))
        override suspend fun cancelProject(id: String): Result<Unit> = Result.success(Unit)

        private fun page(projects: List<Project>) = Pagina(
            conteudo = projects,
            pagina = 0,
            tamanho = 20,
            totalElementos = projects.size.toLong(),
            totalPaginas = 1,
            primeira = true,
            ultima = true
        )
    }
}
