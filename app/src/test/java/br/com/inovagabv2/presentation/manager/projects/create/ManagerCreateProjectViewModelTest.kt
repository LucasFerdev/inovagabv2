package br.com.inovagabv2.presentation.manager.projects.create

import br.com.inovagabv2.domain.model.*
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerCreateProjectViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeProjectRepository: FakeProjectRepository
    private lateinit var fakeStrategyRepository: FakeStrategyRepository
    private lateinit var fakeIdeaRepository: FakeIdeaRepository
    private lateinit var viewModel: ManagerCreateProjectViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeProjectRepository = FakeProjectRepository()
        fakeStrategyRepository = FakeStrategyRepository()
        fakeIdeaRepository = FakeIdeaRepository()

        viewModel = ManagerCreateProjectViewModel(
            fakeProjectRepository,
            fakeStrategyRepository,
            fakeIdeaRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData loads active strategies and approved ideas`() = runTest {
        val state = viewModel.state.first { !it.isLoadingStrategies }

        assertEquals(1, state.activeStrategies.size)
        assertEquals("est1", state.selectedStrategy?.id)
        assertEquals(1, state.approvedIdeas.size)
    }

    @Test
    fun `submitProject creates project with real BigDecimal and real strategy ID`() = runTest {
        viewModel.onNameChange("Sistema de Frota")
        viewModel.onDescriptionChange("Descrição do projeto")
        viewModel.onInvestmentChange("250000,50")
        viewModel.onDeadlineChange("2026-12-31")

        assertTrue(viewModel.state.value.isValid)

        viewModel.submitProject()

        val state = viewModel.state.value

        assertTrue(state.isSuccess)
        assertNotNull(state.createdProject)
        assertEquals("p100", state.createdProject?.id)
        assertEquals("Sistema de Frota", state.createdProject?.name)
        assertEquals(BigDecimal("250000.50"), state.createdProject?.investment)
        assertFalse(state.isSaving)
    }

    private class FakeProjectRepository : ProjectRepository {
        override fun getProjects(): Flow<List<Project>> = flowOf(emptyList())
        override fun getProjectsRemote(status: ProjectStatus?, etapa: ProjectStage?, estrategiaId: String?, gestorId: String?, prazo: String?, pagina: Int, tamanho: Int): Flow<Pagina<Project>> = flowOf(Pagina(emptyList(), 0, 20, 0, 0, true, true))
        override fun getProjectById(id: String): Flow<Project?> = flowOf(null)

        override suspend fun createProject(nome: String, descricao: String, estrategiaId: String, ideiaOrigemId: String?, investimento: BigDecimal, prazo: String): Result<Project> {
            return Result.success(
                Project(
                    id = "p100",
                    name = nome,
                    description = descricao,
                    estrategiaId = estrategiaId,
                    ideiaOrigemId = ideiaOrigemId,
                    investment = investimento,
                    deadline = prazo
                )
            )
        }

        override suspend fun createProject(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun updateProject(id: String, nome: String, descricao: String, estrategiaId: String, ideiaOrigemId: String?, investimento: BigDecimal, prazo: String): Result<Project> = Result.success(Project("p1", nome, descricao))
        override suspend fun updateProject(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun updateProgress(id: String, etapa: ProjectStage, status: ProjectStatus, percentualProgresso: Int, justificativa: String?): Result<Project> = Result.success(Project("p1", "N", "D"))
        override suspend fun registerResults(id: String, retornoFinanceiro: BigDecimal, ganhoProdutividadePercentual: BigDecimal, resultado: String): Result<Project> = Result.success(Project("p1", "N", "D"))
        override suspend fun concludeProject(id: String): Result<Project> = Result.success(Project("p1", "N", "D"))
        override suspend fun cancelProject(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeStrategyRepository : StrategyRepository {
        private val activeStrategy = Strategy("est1", "Excelência", "Desc", status = StrategyStatus.ATIVA)

        override fun getStrategies(): Flow<List<Strategy>> = flowOf(listOf(activeStrategy))
        override fun getStrategiesRemote(status: StrategyStatus?, categoria: String?, campanha: String?, pagina: Int, tamanho: Int): Flow<Pagina<Strategy>> = flowOf(Pagina(listOf(activeStrategy), 0, 20, 1, 1, true, true))
        override fun getActiveStrategies(): Flow<List<Strategy>> = flowOf(listOf(activeStrategy))
        override fun getStrategyById(id: String): Flow<Strategy?> = flowOf(activeStrategy)

        override suspend fun createStrategy(titulo: String, descricao: String, data: String, categoria: String, campanha: String): Result<Strategy> = Result.success(activeStrategy)
        override suspend fun createStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)
        override suspend fun updateStrategy(id: String, titulo: String, descricao: String, data: String, categoria: String, campanha: String): Result<Strategy> = Result.success(activeStrategy)
        override suspend fun updateStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)
        override suspend fun activateStrategy(id: String): Result<Strategy> = Result.success(activeStrategy)
        override suspend fun deactivateStrategy(id: String): Result<Strategy> = Result.success(activeStrategy)
        override suspend fun archiveStrategy(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun deleteStrategy(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeIdeaRepository : IdeaRepository {
        private val approvedIdea = Idea("i1", "Ideia Aprovada", "P", "S", "B", "Operação", "est1", status = IdeaStatus.APROVADA)

        override fun getIdeas(): Flow<List<Idea>> = flowOf(listOf(approvedIdea))
        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flowOf(listOf(approvedIdea))
        override fun getIdeasRemote(status: IdeaStatus?, categoria: String?, estrategiaId: String?, prioridade: Int?, pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(approvedIdea), 0, 20, 1, 1, true, true))
        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(approvedIdea), 0, 20, 1, 1, true, true))
        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(approvedIdea)

        override suspend fun createIdea(titulo: String, problema: String, solucaoProposta: String, beneficiosEsperados: String, categoria: String, estrategiaId: String): Result<Idea> = Result.success(approvedIdea)
        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)
        override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Int?, justificativa: String?): Result<Unit> = Result.success(Unit)
        override suspend fun analisar(id: String): Result<Idea> = Result.success(approvedIdea)
        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> = Result.success(approvedIdea)
        override suspend fun aprovar(id: String): Result<Idea> = Result.success(approvedIdea)
        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> = Result.success(approvedIdea)
        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }
}
