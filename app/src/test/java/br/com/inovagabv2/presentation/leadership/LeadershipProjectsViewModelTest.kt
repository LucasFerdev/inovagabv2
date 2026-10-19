package br.com.inovagabv2.presentation.leadership

import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class LeadershipProjectsViewModelTest {

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
        repository.projects = listOf(Project("1", "Projeto antigo", "Desc"))

        val viewModel = LeadershipProjectsViewModel(repository, FakeAuthRepository())
        val initial = viewModel.projects.first { it.any { project -> project.id == "1" } }

        assertEquals(1, repository.calls)
        assertEquals("Projeto antigo", initial.single().name)
        assertNull(viewModel.error.value)

        repository.projects = listOf(Project("2", "Projeto novo", "Desc"))
        viewModel.refresh()
        val refreshed = viewModel.projects.first { it.any { project -> project.id == "2" } }

        assertEquals(2, repository.calls)
        assertEquals("Projeto novo", refreshed.single().name)
        assertNull(viewModel.error.value)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String): Result<User> =
            Result.success(User("u1", "Lider", email, Role.LIDERANCA))

        override suspend fun register(
            nome: String,
            email: String,
            senha: String,
            empresa: String,
            codigoAcesso: String?
        ): Result<User> = Result.success(User("u1", nome, email, Role.LIDERANCA))

        override suspend fun logout() = Unit
        override fun getCurrentUser(): Flow<User?> = flowOf(User("u1", "Lider", "lider@aguia.com", Role.LIDERANCA))
    }

    private class FakeProjectRepository : ProjectRepository {
        var calls = 0
        var projects: List<Project> = emptyList()

        override fun getProjects(): Flow<List<Project>> = flow {
            calls++
            emit(projects)
        }

        override fun getProjectsRemote(
            status: ProjectStatus?,
            etapa: ProjectStage?,
            estrategiaId: String?,
            gestorId: String?,
            prazo: String?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Project>> = flowOf(
            Pagina(projects, 0, 20, projects.size.toLong(), 1, true, true)
        )

        override fun getProjectById(id: String): Flow<Project?> = flowOf(projects.find { it.id == id })
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
    }
}
