package br.com.inovagabv2.presentation.operator.strategy

import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class OperatorStrategyViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeStrategyRepository: FakeStrategyRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: OperatorStrategyViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeStrategyRepository = FakeStrategyRepository()
        fakeAuthRepository = FakeAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty strategy repository produces empty filteredStrategies and empty categories`() = runTest {
        viewModel = OperatorStrategyViewModel(fakeStrategyRepository, fakeAuthRepository)

        assertTrue(viewModel.filteredStrategies.first().isEmpty())
        assertTrue(viewModel.categories.first().isEmpty())
    }

    @Test
    fun `categories and filteredStrategies calculated dynamically from returned list`() = runTest {
        val list = listOf(
            Strategy("1", "Excelência", "Desc", category = "Experiência", isPublished = true),
            Strategy("2", "Eficiência", "Desc", category = "Sustentabilidade", isPublished = true)
        )
        fakeStrategyRepository.setStrategies(list)
        viewModel = OperatorStrategyViewModel(fakeStrategyRepository, fakeAuthRepository)

        val filtered = viewModel.filteredStrategies.first()
        val categoriesList = viewModel.categories.first()

        assertEquals(2, filtered.size)
        assertEquals(listOf("Experiência", "Sustentabilidade"), categoriesList)

        // Select category "Experiência"
        viewModel.onCategorySelected("Experiência")

        val filteredCategory = viewModel.filteredStrategies.first()
        assertEquals(1, filteredCategory.size)
        assertEquals("Excelência", filteredCategory.first().title)
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
        override fun getCurrentUser(): Flow<User?> = flowOf(User("u1", "Lucas Fernando", "lucas@aguia.com", Role.OPERADOR))
    }
}
