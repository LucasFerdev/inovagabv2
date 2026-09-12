package br.com.inovagabv2.presentation.operator.myideas

import br.com.inovagabv2.core.session.FakeSessionManager
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Priority
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyIdeasViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeIdeaRepository: FakeIdeaRepository
    private lateinit var fakeStrategyRepository: FakeStrategyRepository
    private lateinit var fakeSessionManager: FakeSessionManager
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: MyIdeasViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeIdeaRepository = FakeIdeaRepository()
        fakeStrategyRepository = FakeStrategyRepository()
        fakeSessionManager = FakeSessionManager()
        fakeAuthRepository = FakeAuthRepository()
        viewModel = MyIdeasViewModel(
            fakeIdeaRepository,
            fakeStrategyRepository,
            fakeSessionManager,
            fakeAuthRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty idea repository produces empty ideas list`() = runTest {
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.ideas.value.isEmpty())
    }

    @Test
    fun `creating an idea updates ideas list and maps strategy title correctly`() = runTest {
        val user = User("u1", "Lucas", "lucas@aguia.com", Role.OPERADOR)
        fakeSessionManager.saveSession(user, "token")

        val strategy = Strategy("s1", "Excelência na viagem", "Desc", createdAt = "2026", updatedAt = "2026")
        fakeStrategyRepository.setStrategies(listOf(strategy))

        val idea = Idea(
            id = "i1",
            title = "Otimização do embarque",
            description = "Desc",
            authorId = "u1",
            authorName = "Lucas",
            status = IdeaStatus.EM_ANALISE,
            priority = Priority.ALTA,
            createdAt = "2026-09-10",
            benefits = "Benefits",
            category = "Operação",
            strategyId = "s1"
        )
        fakeIdeaRepository.createIdea(idea)

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.ideas.value.size)
        assertEquals("Otimização do embarque", viewModel.ideas.value.first().title)
        assertEquals("Excelência na viagem", viewModel.strategiesMap.value["s1"])
    }

    private class FakeIdeaRepository : IdeaRepository {
        private val _flow = MutableStateFlow<List<Idea>>(emptyList())

        override fun getIdeas(): Flow<List<Idea>> = _flow

        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = _flow

        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(null)

        override suspend fun createIdea(idea: Idea): Result<Unit> {
            val list = _flow.value.toMutableList()
            list.add(0, idea)
            _flow.value = list
            return Result.success(Unit)
        }

        override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Priority?): Result<Unit> = Result.success(Unit)
    }

    private class FakeStrategyRepository : StrategyRepository {
        private val _flow = MutableStateFlow<List<Strategy>>(emptyList())

        fun setStrategies(list: List<Strategy>) {
            _flow.value = list
        }

        override fun getStrategies(): Flow<List<Strategy>> = _flow

        override fun getStrategyById(id: String): Flow<Strategy?> = flowOf(null)

        override suspend fun createStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)

        override suspend fun updateStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)

        override suspend fun deleteStrategy(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String) = Result.success(User("u1", "Name", email, Role.OPERADOR))
        override suspend fun register(nome: String, email: String, senha: String, empresa: String, codigoAcesso: String?) = Result.success(User("u1", nome, email, Role.OPERADOR))
        override suspend fun logout() {}
        override fun getCurrentUser(): Flow<User?> = flowOf(User("u1", "Lucas", "lucas@aguia.com", Role.OPERADOR))
    }
}
