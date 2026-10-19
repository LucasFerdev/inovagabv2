package br.com.inovagabv2.presentation.operator.details

import androidx.lifecycle.SavedStateHandle
import br.com.inovagabv2.core.util.NO_CONNECTION_MESSAGE
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
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
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class IdeaDetailsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `IOException ao carregar ideia vai para erro sem lancar`() = runTest {
        val viewModel = IdeaDetailsViewModel(
            ideaRepository = FakeIdeaRepository(error = IOException("sem rede")),
            aiRepository = FakeAiRepository(),
            authRepository = FakeAuthRepository(),
            savedStateHandle = SavedStateHandle(mapOf("ideaId" to "i1"))
        )

        assertFalse(viewModel.isLoadingIdea.value)
        assertNull(viewModel.idea.value)
        assertEquals(NO_CONNECTION_MESSAGE, viewModel.loadError.value)
    }

    private class FakeIdeaRepository(
        private val error: Throwable? = null
    ) : IdeaRepository {
        override fun getIdeas(): Flow<List<Idea>> = flowOf(emptyList())
        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flowOf(emptyList())
        override fun getIdeasRemote(
            status: IdeaStatus?,
            categoria: String?,
            estrategiaId: String?,
            prioridade: Int?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Idea>> = flowOf(Pagina(emptyList(), 0, 20, 0, 1, true, true))

        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> =
            flowOf(Pagina(emptyList(), 0, 20, 0, 1, true, true))

        override fun getIdeaById(id: String): Flow<Idea?> = flow {
            error?.let { throw it }
            emit(null)
        }

        override fun consultarHistorico(id: String): Flow<List<HistoryItem>> = flowOf(emptyList())
        override suspend fun createIdea(
            titulo: String,
            problema: String,
            solucaoProposta: String,
            beneficiosEsperados: String,
            categoria: String,
            estrategiaId: String
        ): Result<Idea> = Result.success(Idea("1", titulo, category = categoria))

        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)
        override suspend fun updateIdeaStatus(
            id: String,
            status: IdeaStatus,
            priority: Int?,
            justificativa: String?
        ): Result<Unit> = Result.success(Unit)

        override suspend fun analisar(id: String): Result<Idea> = Result.success(Idea(id, "T", category = "C"))
        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> =
            Result.success(Idea(id, "T", category = "C"))

        override suspend fun aprovar(id: String): Result<Idea> = Result.success(Idea(id, "T", category = "C"))
        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> =
            Result.success(Idea(id, "T", category = "C"))

        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeAiRepository : AiRepository {
        override suspend fun analyzeIdea(ideaId: String, recalculate: Boolean): Result<AiAnalysis> =
            Result.failure(Exception("não usado"))

        override suspend fun getIdeaAnalysis(ideaId: String): Result<AiAnalysis> =
            Result.failure(Exception("Esta ideia ainda não possui análise da IA."))
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String): Result<User> =
            Result.success(User("u1", "Op", email, Role.OPERADOR))

        override suspend fun register(
            nome: String,
            email: String,
            senha: String,
            empresa: String,
            codigoAcesso: String?
        ): Result<User> = Result.success(User("u1", nome, email, Role.OPERADOR))

        override suspend fun logout() = Unit
        override fun getCurrentUser(): Flow<User?> = flowOf(User("u1", "Op", "op@aguia.com", Role.OPERADOR))
    }
}
