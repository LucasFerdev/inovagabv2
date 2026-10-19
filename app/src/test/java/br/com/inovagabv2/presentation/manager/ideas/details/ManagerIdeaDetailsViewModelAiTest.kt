package br.com.inovagabv2.presentation.manager.ideas.details

import androidx.lifecycle.SavedStateHandle
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.core.util.SERVER_STARTING_MESSAGE
import kotlinx.coroutines.CompletableDeferred
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerIdeaDetailsViewModelAiTest {

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
    fun `loadAiAnalysis loads saved analysis if present`() = runTest {
        val analysis = AiAnalysis(
            ideaId = "i100",
            overallScore = 90,
            suggestedPriority = 4,
            executiveSummary = "Resumo",
            model = "gemini-1.5-flash",
            generatedAt = "2026-09-13"
        )
        fakeAiRepository.savedAnalysis = analysis
        viewModel.loadAiAnalysis()

        val state = viewModel.state.first { !it.isLoadingAi }

        assertNotNull(state.aiAnalysis)
        assertEquals(90, state.aiAnalysis?.overallScore)
    }

    @Test
    fun `onAnalyzeAi performs new analysis and sets feedback`() = runTest {
        viewModel.onAnalyzeAi()

        val state = viewModel.state.value

        assertNotNull(state.aiAnalysis)
        assertEquals("Análise realizada com sucesso.", state.feedbackMessage)
        assertFalse(state.isSubmittingAi)
    }

    @Test
    fun `onConfirmRecalculateAi recalculates and updates aiAnalysis`() = runTest {
        viewModel.onAnalyzeAi()
        val initialAnalysis = viewModel.state.value.aiAnalysis

        viewModel.onConfirmRecalculateAi()
        val state = viewModel.state.value

        assertNotNull(state.aiAnalysis)
        assertEquals("Análise recalculada com sucesso.", state.feedbackMessage)
        assertFalse(state.isSubmittingAi)
    }

    @Test
    fun `patch em analise seguido de falha da ia mantem status e o botao de analisar`() = runTest {
        fakeAiRepository.analyzeError = SocketTimeoutException("timeout")

        viewModel.onAnalyzeAi()

        val state = viewModel.state.value

        assertEquals(1, fakeIdeaRepository.analisarCalls)
        assertEquals(1, fakeAiRepository.analyzeCalls)
        assertEquals(IdeaStatus.EM_ANALISE, state.idea?.status)
        assertNull(state.aiAnalysis)
        assertFalse(state.isSubmittingAi)
        assertEquals(SERVER_STARTING_MESSAGE, state.errorMessage)
        assertTrue(showAnalyzeWithAi(state.idea!!.status, hasAnalysis = false))
        assertFalse(showRecalculateAi(state.idea!!.status, hasAnalysis = false))
    }

    @Test
    fun `toques repetidos em analisar com ia sao ignorados enquanto a requisicao esta em andamento`() = runTest {
        fakeAiRepository.analyzeGate = CompletableDeferred()

        viewModel.onAnalyzeAi()
        viewModel.onAnalyzeAi()

        assertEquals(1, fakeIdeaRepository.analisarCalls)
        assertEquals(1, fakeAiRepository.analyzeCalls)
        assertTrue(viewModel.state.value.isSubmittingAi)

        fakeAiRepository.analyzeGate?.complete(
            Result.failure(SocketTimeoutException("timeout"))
        )

        val state = viewModel.state.value
        assertFalse(state.isSubmittingAi)
        assertEquals(IdeaStatus.EM_ANALISE, state.idea?.status)
        assertNull(state.aiAnalysis)
    }

    @Test
    fun `failed recalculation preserves previous valid aiAnalysis`() = runTest {
        viewModel.onAnalyzeAi()
        val previousAnalysis = viewModel.state.value.aiAnalysis

        fakeAiRepository.shouldFailRecalculate = true
        viewModel.onConfirmRecalculateAi()

        val state = viewModel.state.value

        assertEquals(previousAnalysis, state.aiAnalysis)
        assertEquals("O serviço de IA está indisponível no momento.", state.errorMessage)
        assertFalse(state.isSubmittingAi)
    }

    private class FakeIdeaRepository : IdeaRepository {
        var analisarCalls = 0
        private val idea = Idea("i100", "Title", "Prob", "Sol", "Ben", "Operação", "s1")

        override fun getIdeas(): Flow<List<Idea>> = flowOf(listOf(idea))
        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flowOf(listOf(idea))
        override fun getIdeasRemote(status: IdeaStatus?, categoria: String?, estrategiaId: String?, prioridade: Int?, pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(idea), 0, 20, 1, 1, true, true))
        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(idea), 0, 20, 1, 1, true, true))
        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(idea)
        override fun consultarHistorico(id: String): Flow<List<br.com.inovagabv2.domain.model.HistoryItem>> = flowOf(emptyList())

        override suspend fun createIdea(titulo: String, problema: String, solucaoProposta: String, beneficiosEsperados: String, categoria: String, estrategiaId: String): Result<Idea> = Result.success(idea)
        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)
        override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Int?, justificativa: String?): Result<Unit> = Result.success(Unit)
        override suspend fun analisar(id: String): Result<Idea> {
            analisarCalls++
            return Result.success(idea.copy(status = IdeaStatus.EM_ANALISE))
        }
        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> = Result.success(idea)
        override suspend fun aprovar(id: String): Result<Idea> = Result.success(idea)
        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> = Result.success(idea)
        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeAiRepository : AiRepository {
        var savedAnalysis: AiAnalysis? = null
        var shouldFailRecalculate = false
        var analyzeError: Throwable? = null
        var analyzeCalls = 0
        var analyzeGate: CompletableDeferred<Result<AiAnalysis>>? = null

        override suspend fun analyzeIdea(ideaId: String, recalculate: Boolean): Result<AiAnalysis> {
            analyzeCalls++
            analyzeGate?.let { return it.await() }
            if (recalculate && shouldFailRecalculate) {
                return Result.failure(Exception("O serviço de IA está indisponível no momento."))
            }
            if (!recalculate && analyzeError != null) {
                return Result.failure(analyzeError!!)
            }
            val analysis = AiAnalysis(
                ideaId = ideaId,
                overallScore = if (recalculate) 95 else 85,
                suggestedPriority = 4,
                executiveSummary = "Resumo",
                model = "gemini-1.5-flash",
                generatedAt = "2026-09-13"
            )
            savedAnalysis = analysis
            return Result.success(analysis)
        }

        override suspend fun getIdeaAnalysis(ideaId: String): Result<AiAnalysis> {
            return if (savedAnalysis != null) {
                Result.success(savedAnalysis!!)
            } else {
                Result.failure(Exception("Esta ideia ainda não possui análise da IA."))
            }
        }
    }
}
