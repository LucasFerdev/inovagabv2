package br.com.inovagabv2.presentation.manager.ideas.details

import androidx.lifecycle.SavedStateHandle
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerIdeaDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeIdeaRepository: FakeIdeaRepository
    private lateinit var fakeAiRepository: FakeAiRepository
    private lateinit var viewModel: ManagerIdeaDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeIdeaRepository = FakeIdeaRepository()
        fakeAiRepository = FakeAiRepository()
        val savedStateHandle = SavedStateHandle(mapOf("ideaId" to "i100"))
        viewModel = ManagerIdeaDetailsViewModel(fakeIdeaRepository, fakeAiRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadIdea loads idea from repository`() = runTest {
        val state = viewModel.state.first { !it.isLoading }

        assertEquals("i100", state.idea?.id)
        assertEquals(IdeaStatus.ENVIADA, state.idea?.status)
    }

    @Test
    fun `onAnalisar updates status to EM_ANALISE and sets feedback message`() = runTest {
        viewModel.onAnalisar()

        val state = viewModel.state.value

        assertEquals(IdeaStatus.EM_ANALISE, state.idea?.status)
        assertEquals("Ideia enviada para análise.", state.feedbackMessage)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `onConfirmApproval approves idea and sets feedback and navigate back flag`() = runTest {
        viewModel.onPrioritySelected(4)
        viewModel.onShowApprovalDialog()
        viewModel.onConfirmApproval()

        val state = viewModel.state.value

        assertEquals(IdeaStatus.APROVADA, state.idea?.status)
        assertEquals(4, state.idea?.priority)
        assertEquals("Ideia aprovada com sucesso.", state.feedbackMessage)
        assertTrue(state.shouldNavigateBack)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `onConfirmRejection validates reason and sets feedback message`() = runTest {
        viewModel.onShowRejectionDialog()
        viewModel.onRejectionReasonChange("Custo de implementação muito elevado")
        viewModel.onConfirmRejection()

        val state = viewModel.state.value

        assertEquals(IdeaStatus.REJEITADA, state.idea?.status)
        assertEquals("Ideia rejeitada com sucesso.", state.feedbackMessage)
        assertTrue(state.shouldNavigateBack)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `API error on approval does not change idea status and sets error message`() = runTest {
        fakeIdeaRepository.shouldFail = true
        viewModel.onConfirmApproval()

        val state = viewModel.state.value

        assertEquals(IdeaStatus.ENVIADA, state.idea?.status)
        assertEquals("Erro na API de aprovação", state.errorMessage)
        assertFalse(state.isSubmitting)
        assertFalse(state.shouldNavigateBack)
    }

    private class FakeIdeaRepository : IdeaRepository {
        var shouldFail = false
        private var currentIdea = Idea(
            id = "i100",
            title = "Test Idea",
            problem = "Prob",
            proposedSolution = "Sol",
            expectedBenefits = "Ben",
            category = "Operação",
            strategyId = "s1",
            status = IdeaStatus.ENVIADA
        )

        override fun getIdeas(): Flow<List<Idea>> = flowOf(listOf(currentIdea))
        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flowOf(listOf(currentIdea))
        override fun getIdeasRemote(status: IdeaStatus?, categoria: String?, estrategiaId: String?, prioridade: Int?, pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(currentIdea), 0, 20, 1, 1, true, true))
        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(currentIdea), 0, 20, 1, 1, true, true))
        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(currentIdea)
        override fun consultarHistorico(id: String): Flow<List<br.com.inovagabv2.domain.model.HistoryItem>> = flowOf(emptyList())

        override suspend fun createIdea(titulo: String, problema: String, solucaoProposta: String, beneficiosEsperados: String, categoria: String, estrategiaId: String): Result<Idea> = Result.success(currentIdea)
        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)
        override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Int?, justificativa: String?): Result<Unit> = Result.success(Unit)

        override suspend fun analisar(id: String): Result<Idea> {
            if (shouldFail) return Result.failure(Exception("Erro na API de análise"))
            currentIdea = currentIdea.copy(status = IdeaStatus.EM_ANALISE)
            return Result.success(currentIdea)
        }

        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> {
            if (shouldFail) return Result.failure(Exception("Erro na API de priorização"))
            currentIdea = currentIdea.copy(priority = prioridade)
            return Result.success(currentIdea)
        }

        override suspend fun aprovar(id: String): Result<Idea> {
            if (shouldFail) return Result.failure(Exception("Erro na API de aprovação"))
            currentIdea = currentIdea.copy(status = IdeaStatus.APROVADA)
            return Result.success(currentIdea)
        }

        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> {
            if (shouldFail) return Result.failure(Exception("Erro na API de rejeição"))
            currentIdea = currentIdea.copy(status = IdeaStatus.REJEITADA, evaluationJustification = justificativa)
            return Result.success(currentIdea)
        }

        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeAiRepository : AiRepository {
        override suspend fun analyzeIdea(ideaId: String, recalculate: Boolean): Result<AiAnalysis> {
            return Result.failure(Exception("Sem análise"))
        }

        override suspend fun getIdeaAnalysis(ideaId: String): Result<AiAnalysis> {
            return Result.failure(Exception("Esta ideia ainda não possui análise da IA."))
        }
    }
}
