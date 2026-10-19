package br.com.inovagabv2.presentation.operator.home

import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.StrategyStatus
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class OperatorHomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeIdeaRepository: FakeIdeaRepository
    private lateinit var fakeStrategyRepository: FakeStrategyRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeIdeaRepository = FakeIdeaRepository()
        fakeStrategyRepository = FakeStrategyRepository()
        fakeAuthRepository = FakeAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sucesso carrega ideias do autor e estrategias ativas`() = runTest {
        val user = User("u1", "Maria Operadora", "maria@aguia.com", Role.OPERADOR)
        fakeAuthRepository.setUser(user)

        val ideas = listOf(
            idea("i1", "Ideia antiga", IdeaStatus.ENVIADA, updatedAt = "2026-01-01"),
            idea("i2", "Ideia em analise", IdeaStatus.EM_ANALISE, updatedAt = "2026-03-01"),
            idea("i3", "Ideia aprovada", IdeaStatus.APROVADA, createdAt = "2026-04-01"),
            idea("i4", "Ideia recente enviada", IdeaStatus.ENVIADA, updatedAt = "2026-02-01")
        )
        fakeIdeaRepository.setIdeas(ideas)
        fakeStrategyRepository.setActiveStrategies(
            listOf(
                Strategy("s1", "Estrategia ativa 1", "Desc", status = StrategyStatus.ATIVA),
                Strategy("s2", "Estrategia ativa 2", "Desc", status = StrategyStatus.ATIVA),
                Strategy("s3", "Estrategia ativa 3", "Desc", status = StrategyStatus.ATIVA)
            )
        )
        fakeStrategyRepository.setAllStrategies(
            listOf(Strategy("s9", "Estrategia geral", "Desc", status = StrategyStatus.RASCUNHO))
        )

        val viewModel = OperatorHomeViewModel(
            fakeIdeaRepository,
            fakeStrategyRepository,
            fakeAuthRepository
        )

        val state = viewModel.state.first { !it.isLoading && it.recentIdeas.size == 3 }

        assertEquals("u1", fakeIdeaRepository.lastAuthorId)
        assertEquals(0, fakeIdeaRepository.getIdeasCalls)
        assertTrue(fakeStrategyRepository.activeCalls > 0)
        assertEquals(0, fakeStrategyRepository.allCalls)
        assertNull(state.error)
        assertEquals("Maria Operadora", state.userName)
        assertEquals(2, state.sentCount)
        assertEquals(1, state.inAnalysisCount)
        assertEquals(1, state.approvedCount)
        assertEquals(listOf("i3", "i2", "i4"), state.recentIdeas.map { it.id })
        assertEquals(listOf("s1", "s2"), state.strategies.map { it.id })
    }

    @Test
    fun `IOException leva ao estado de erro`() = runTest {
        fakeAuthRepository.setUser(User("u1", "Maria", "maria@aguia.com", Role.OPERADOR))
        fakeIdeaRepository.ideasError = IOException("falha de rede")

        val viewModel = OperatorHomeViewModel(
            fakeIdeaRepository,
            fakeStrategyRepository,
            fakeAuthRepository
        )

        val state = viewModel.state.first { !it.isLoading && it.error != null }

        assertEquals(false, state.isLoading)
        assertEquals(
            "Não foi possível carregar os dados. Verifique sua conexão e tente novamente.",
            state.error
        )
        assertEquals("u1", fakeIdeaRepository.lastAuthorId)
        assertEquals(0, fakeIdeaRepository.getIdeasCalls)
    }

    @Test
    fun `usuario nulo ou sem id nao chama getIdeasByAuthor`() = runTest {
        fakeAuthRepository.setUser(null)
        val withoutUser = OperatorHomeViewModel(
            fakeIdeaRepository,
            fakeStrategyRepository,
            fakeAuthRepository
        )
        val nullUserState = withoutUser.state.first { !it.isLoading && it.error != null }

        assertEquals(false, nullUserState.isLoading)
        assertEquals(
            "Não foi possível identificar o usuário. Entre novamente para continuar.",
            nullUserState.error
        )
        assertEquals(0, fakeIdeaRepository.byAuthorCalls)

        fakeAuthRepository.setUser(User("   ", "Maria", "maria@aguia.com", Role.OPERADOR))
        withoutUser.refresh()
        val blankIdState = withoutUser.state.first { !it.isLoading && it.error != null }

        assertEquals(false, blankIdState.isLoading)
        assertEquals(
            "Não foi possível identificar o usuário. Entre novamente para continuar.",
            blankIdState.error
        )
        assertEquals(0, fakeIdeaRepository.byAuthorCalls)
    }

    @Test
    fun `refresh com ideias na tela nao volta para carregamento`() = runTest {
        fakeAuthRepository.setUser(User("u1", "Maria", "maria@aguia.com", Role.OPERADOR))
        fakeIdeaRepository.setIdeas(listOf(idea("i1", "Ideia", IdeaStatus.ENVIADA)))
        val viewModel = OperatorHomeViewModel(
            fakeIdeaRepository,
            fakeStrategyRepository,
            fakeAuthRepository
        )
        viewModel.state.first { !it.isLoading && it.recentIdeas.isNotEmpty() }

        val gate = CompletableDeferred<Unit>()
        fakeIdeaRepository.nextAuthorGate = gate
        viewModel.refresh()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(listOf("i1"), viewModel.state.value.recentIdeas.map { it.id })

        gate.complete(Unit)
        val refreshed = viewModel.state.first { !it.isLoading && it.recentIdeas.isNotEmpty() }
        assertFalse(refreshed.isLoading)
        assertEquals(listOf("i1"), refreshed.recentIdeas.map { it.id })
    }

    private fun idea(
        id: String,
        title: String,
        status: IdeaStatus,
        createdAt: String? = null,
        updatedAt: String? = null
    ) = Idea(
        id = id,
        title = title,
        category = "Operação",
        authorId = "u1",
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private class FakeIdeaRepository : IdeaRepository {
        private val ideas = MutableStateFlow<List<Idea>>(emptyList())
        var ideasError: Throwable? = null
        var getIdeasCalls: Int = 0
        var byAuthorCalls: Int = 0
        var lastAuthorId: String? = null
        var nextAuthorGate: CompletableDeferred<Unit>? = null

        fun setIdeas(list: List<Idea>) {
            ideas.value = list
        }

        override fun getIdeas(): Flow<List<Idea>> {
            getIdeasCalls++
            return ideas
        }

        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> {
            byAuthorCalls++
            lastAuthorId = authorId
            val error = ideasError
            if (error != null) {
                return flow { throw error }
            }
            val gate = nextAuthorGate
            if (gate != null) {
                nextAuthorGate = null
                return flow {
                    gate.await()
                    emit(ideas.value)
                }
            }
            return ideas
        }

        override fun getIdeasRemote(
            status: IdeaStatus?,
            categoria: String?,
            estrategiaId: String?,
            prioridade: Int?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Idea>> = flowOf(page(ideas.value))

        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flowOf(page(ideas.value))

        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(null)

        override fun consultarHistorico(id: String): Flow<List<HistoryItem>> = flowOf(emptyList())

        override suspend fun createIdea(
            titulo: String,
            problema: String,
            solucaoProposta: String,
            beneficiosEsperados: String,
            categoria: String,
            estrategiaId: String
        ): Result<Idea> = Result.success(Idea("i1", titulo, problema, solucaoProposta, beneficiosEsperados, categoria, estrategiaId))

        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)

        override suspend fun updateIdeaStatus(
            id: String,
            status: IdeaStatus,
            priority: Int?,
            justificativa: String?
        ): Result<Unit> = Result.success(Unit)

        override suspend fun analisar(id: String): Result<Idea> = Result.success(Idea("1", "T", category = "C"))

        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> =
            Result.success(Idea("1", "T", category = "C"))

        override suspend fun aprovar(id: String): Result<Idea> = Result.success(Idea("1", "T", category = "C"))

        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> =
            Result.success(Idea("1", "T", category = "C"))

        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)

        private fun <T> page(conteudo: List<T>) = Pagina(
            conteudo = conteudo,
            pagina = 0,
            tamanho = 20,
            totalElementos = conteudo.size.toLong(),
            totalPaginas = 1,
            primeira = true,
            ultima = true
        )
    }

    private class FakeStrategyRepository : StrategyRepository {
        private val allStrategies = MutableStateFlow<List<Strategy>>(emptyList())
        private val activeStrategies = MutableStateFlow<List<Strategy>>(emptyList())
        var allCalls: Int = 0
        var activeCalls: Int = 0

        fun setAllStrategies(list: List<Strategy>) {
            allStrategies.value = list
        }

        fun setActiveStrategies(list: List<Strategy>) {
            activeStrategies.value = list
        }

        override fun getStrategies(): Flow<List<Strategy>> {
            allCalls++
            return allStrategies
        }

        override fun getStrategiesRemote(
            status: StrategyStatus?,
            categoria: String?,
            campanha: String?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Strategy>> = flowOf(
            Pagina(allStrategies.value, 0, 20, allStrategies.value.size.toLong(), 1, true, true)
        )

        override fun getActiveStrategies(): Flow<List<Strategy>> {
            activeCalls++
            return activeStrategies
        }

        override fun getStrategyById(id: String): Flow<Strategy?> = flowOf(null)

        override fun consultarHistorico(id: String): Flow<List<HistoryItem>> = flowOf(emptyList())

        override suspend fun createStrategy(
            titulo: String,
            descricao: String,
            data: String,
            categoria: String,
            campanha: String
        ): Result<Strategy> = Result.success(Strategy("1", titulo, descricao))

        override suspend fun createStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)

        override suspend fun updateStrategy(
            id: String,
            titulo: String,
            descricao: String,
            data: String,
            categoria: String,
            campanha: String
        ): Result<Strategy> = Result.success(Strategy("1", titulo, descricao))

        override suspend fun updateStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)

        override suspend fun activateStrategy(id: String): Result<Strategy> =
            Result.success(Strategy("1", "T", "D", status = StrategyStatus.ATIVA))

        override suspend fun deactivateStrategy(id: String): Result<Strategy> =
            Result.success(Strategy("1", "T", "D", status = StrategyStatus.INATIVA))

        override suspend fun archiveStrategy(id: String): Result<Unit> = Result.success(Unit)

        override suspend fun deleteStrategy(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeAuthRepository : AuthRepository {
        private val currentUser = MutableStateFlow<User?>(null)

        fun setUser(user: User?) {
            currentUser.value = user
        }

        override suspend fun login(email: String, password: String): Result<User> =
            Result.success(User("u1", "Name", email, Role.OPERADOR))

        override suspend fun register(
            nome: String,
            email: String,
            senha: String,
            empresa: String,
            codigoAcesso: String?
        ): Result<User> = Result.success(User("u1", nome, email, Role.OPERADOR))

        override suspend fun logout() {}

        override fun getCurrentUser(): Flow<User?> = currentUser
    }
}
