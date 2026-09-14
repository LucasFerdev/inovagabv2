package br.com.inovagabv2.presentation.operator.details

import androidx.lifecycle.SavedStateHandle
import br.com.inovagabv2.domain.model.*
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.AuthRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IdeaDetailsViewModelAiTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeIdeaRepository: FakeIdeaRepository
    private lateinit var fakeAiRepository: FakeAiRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: IdeaDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeIdeaRepository = FakeIdeaRepository()
        fakeAiRepository = FakeAiRepository()
        fakeAuthRepository = FakeAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Lideranca user queries AI analysis and displays result`() = runTest {
        fakeAuthRepository.currentUserRole = Role.LIDERANCA
        fakeAiRepository.savedAnalysis = AiAnalysis(
            ideaId = "i100",
            overallScore = 95,
            suggestedPriority = 5,
            executiveSummary = "Excelente proposta",
            model = "gemini-1.5-flash",
            generatedAt = "2026-09-13"
        )

        val savedStateHandle = SavedStateHandle(mapOf("ideaId" to "i100"))
        viewModel = IdeaDetailsViewModel(fakeIdeaRepository, fakeAiRepository, fakeAuthRepository, savedStateHandle)

        val aiAnalysis = viewModel.aiAnalysis.first { it != null }

        assertNotNull(aiAnalysis)
        assertEquals(95, aiAnalysis?.overallScore)
        assertEquals("gemini-1.5-flash", aiAnalysis?.model)
    }

    @Test
    fun `Lideranca user handles 404 no AI analysis with message`() = runTest {
        fakeAuthRepository.currentUserRole = Role.LIDERANCA
        fakeAiRepository.savedAnalysis = null

        val savedStateHandle = SavedStateHandle(mapOf("ideaId" to "i100"))
        viewModel = IdeaDetailsViewModel(fakeIdeaRepository, fakeAiRepository, fakeAuthRepository, savedStateHandle)

        val aiError = viewModel.aiError.first { !it.isNullOrBlank() }

        assertNull(viewModel.aiAnalysis.value)
        assertEquals("Esta ideia ainda não possui análise da IA.", aiError)
    }

    @Test
    fun `Operador user does NOT query or receive AI analysis`() = runTest {
        fakeAuthRepository.currentUserRole = Role.OPERADOR
        fakeAiRepository.savedAnalysis = AiAnalysis("i100", 99, 5, "S", model = "m", generatedAt = "d")

        val savedStateHandle = SavedStateHandle(mapOf("ideaId" to "i100"))
        viewModel = IdeaDetailsViewModel(fakeIdeaRepository, fakeAiRepository, fakeAuthRepository, savedStateHandle)

        assertNull(viewModel.aiAnalysis.value)
        assertNull(viewModel.aiError.value)
    }

    private class FakeIdeaRepository : IdeaRepository {
        private val idea = Idea("i100", "Title", "Prob", "Sol", "Ben", "Operação", "s1")

        override fun getIdeas(): Flow<List<Idea>> = flowOf(listOf(idea))
        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flowOf(listOf(idea))
        override fun getIdeasRemote(status: IdeaStatus?, categoria: String?, estrategiaId: String?, prioridade: Int?, pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(idea), 0, 20, 1, 1, true, true))
        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(Pagina(listOf(idea), 0, 20, 1, 1, true, true))
        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(idea)

        override suspend fun createIdea(titulo: String, problema: String, solucaoProposta: String, beneficiosEsperados: String, categoria: String, estrategiaId: String): Result<Idea> = Result.success(idea)
        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)
        override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Int?, justificativa: String?): Result<Unit> = Result.success(Unit)
        override suspend fun analisar(id: String): Result<Idea> = Result.success(idea)
        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> = Result.success(idea)
        override suspend fun aprovar(id: String): Result<Idea> = Result.success(idea)
        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> = Result.success(idea)
        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeAiRepository : AiRepository {
        var savedAnalysis: AiAnalysis? = null

        override suspend fun analyzeIdea(ideaId: String, recalculate: Boolean): Result<AiAnalysis> {
            return Result.failure(Exception("Not supported in test"))
        }

        override suspend fun getIdeaAnalysis(ideaId: String): Result<AiAnalysis> {
            return if (savedAnalysis != null) {
                Result.success(savedAnalysis!!)
            } else {
                Result.failure(Exception("Esta ideia ainda não possui análise da IA."))
            }
        }
    }

    private class FakeAuthRepository : AuthRepository {
        var currentUserRole: Role = Role.LIDERANCA

        override suspend fun login(email: String, password: String) = Result.success(User("u1", "User", email, currentUserRole))
        override suspend fun register(nome: String, email: String, senha: String, empresa: String, codigoAcesso: String?) = Result.success(User("u1", nome, email, currentUserRole))
        override suspend fun logout() {}
        override fun getCurrentUser(): Flow<User?> = flowOf(User("u1", "User Test", "user@aguia.com", currentUserRole))
    }
}
