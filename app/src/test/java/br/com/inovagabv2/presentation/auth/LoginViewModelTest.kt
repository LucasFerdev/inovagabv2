package br.com.inovagabv2.presentation.auth

import br.com.inovagabv2.core.util.SERVER_STARTING_MESSAGE
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @Test
    fun `timeout no login mostra mensagem de servidor iniciando`() = runLoginTest {
        val viewModel = LoginViewModel(
            FakeAuthRepository(result = Result.failure(SocketTimeoutException("timeout")))
        )

        viewModel.login("user@aguia.com", "senha123")
        advanceUntilIdle()

        assertEquals(SERVER_STARTING_MESSAGE, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.showConnectingHint)
        assertNull(viewModel.uiState.value.successUser)
    }

    @Test
    fun `carga maior que 5s liga e desliga aviso de conexao`() = runLoginTest {
        val viewModel = LoginViewModel(
            FakeAuthRepository(delayMillis = 10_000)
        )

        viewModel.login("user@aguia.com", "senha123")
        advanceTimeBy(4_999)
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.showConnectingHint)

        advanceTimeBy(2)
        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showConnectingHint)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.showConnectingHint)
        assertEquals("user@aguia.com", viewModel.uiState.value.successUser?.email)
    }

    private fun runLoginTest(testBody: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            testBody()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FakeAuthRepository(
        private val result: Result<User>? = null,
        private val delayMillis: Long = 0L
    ) : AuthRepository {
        override suspend fun login(email: String, password: String): Result<User> {
            if (delayMillis > 0) delay(delayMillis)
            return result ?: Result.success(User("u1", "Nome", email, Role.OPERADOR))
        }

        override suspend fun register(
            nome: String,
            email: String,
            senha: String,
            empresa: String,
            codigoAcesso: String?
        ): Result<User> = Result.success(User("u1", nome, email, Role.OPERADOR))

        override suspend fun logout() = Unit

        override fun getCurrentUser(): Flow<User?> = flowOf(null)
    }
}
