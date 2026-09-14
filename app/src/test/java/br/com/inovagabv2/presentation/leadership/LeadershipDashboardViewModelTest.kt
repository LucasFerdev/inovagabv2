package br.com.inovagabv2.presentation.leadership

import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.model.DashboardProjectDetails
import br.com.inovagabv2.domain.model.DashboardStrategyDetails
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.DashboardRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class LeadershipDashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeDashboardRepository: FakeDashboardRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: LeadershipDashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDashboardRepository = FakeDashboardRepository()
        fakeAuthRepository = FakeAuthRepository()
        viewModel = LeadershipDashboardViewModel(fakeDashboardRepository, fakeAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDashboardData updates state with dashboard data and user`() = runTest {
        viewModel.loadDashboardData()

        val state = viewModel.state.first { !it.isLoading }
        val user = viewModel.user.first()

        assertNotNull(state.data)
        assertEquals("R$ 100 mil", state.data?.profit)
        assertEquals("Carlos Mendes", user?.name)
        assertEquals(Role.LIDERANCA, user?.role)
    }

    private class FakeDashboardRepository : DashboardRepository {
        private val _data = MutableStateFlow(
            DashboardData(
                lucroObtido = BigDecimal("100000.00"),
                roiPercentual = BigDecimal("25.0"),
                totalEmAndamento = 5,
                ideiasAprovadas = 10
            )
        )

        override fun getDashboardData(): Flow<DashboardData> = _data
        override fun getDashboardStrategyDetails(strategyId: String): Flow<DashboardStrategyDetails?> = flowOf(null)
        override fun getDashboardProjectDetails(projectId: String): Flow<DashboardProjectDetails?> = flowOf(null)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String) = Result.success(User("l1", "Carlos Mendes", email, Role.LIDERANCA))
        override suspend fun register(nome: String, email: String, senha: String, empresa: String, codigoAcesso: String?) = Result.success(User("l1", nome, email, Role.LIDERANCA))
        override suspend fun logout() {}
        override fun getCurrentUser(): Flow<User?> = flowOf(User("l1", "Carlos Mendes", "carlos@aguia.com", Role.LIDERANCA))
    }
}
