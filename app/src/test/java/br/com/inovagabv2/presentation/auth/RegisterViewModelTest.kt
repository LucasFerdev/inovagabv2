package br.com.inovagabv2.presentation.auth

import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAuthRepository()
        viewModel = RegisterViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register without access code sends null codigoAcesso`() = runTest {
        viewModel.register(
            fullName = "Usuário Teste",
            email = "teste@aguia.com",
            companyUnit = "Águia Branca",
            password = "password123",
            confirmPassword = "password123",
            acceptedTerms = true,
            accessCode = null
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(fakeRepository.lastCodigoAcesso)
        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `register with empty access code sends null codigoAcesso`() = runTest {
        viewModel.register(
            fullName = "Usuário Teste",
            email = "teste@aguia.com",
            companyUnit = "Águia Branca",
            password = "password123",
            confirmPassword = "password123",
            acceptedTerms = true,
            accessCode = "   "
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(fakeRepository.lastCodigoAcesso)
        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `register with access code sends normalized string`() = runTest {
        viewModel.register(
            fullName = "Usuário Gestor",
            email = "gestor@aguia.com",
            companyUnit = "Águia Branca",
            password = "password123",
            confirmPassword = "password123",
            acceptedTerms = true,
            accessCode = "  GEST2026  "
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("GEST2026", fakeRepository.lastCodigoAcesso)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertEquals(Role.GESTOR, viewModel.uiState.value.createdUser?.role)
    }

    @Test
    fun `register backend 400 error is shown to user`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Código de acesso inválido."

        viewModel.register(
            fullName = "Usuário Teste",
            email = "teste@aguia.com",
            companyUnit = "Águia Branca",
            password = "password123",
            confirmPassword = "password123",
            acceptedTerms = true,
            accessCode = "INVALIDO"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals("Código de acesso inválido.", viewModel.uiState.value.error)
    }

    private class FakeAuthRepository : AuthRepository {
        var lastCodigoAcesso: String? = null
        var shouldReturnError: Boolean = false
        var errorMessage: String = "Erro no cadastro"

        override suspend fun login(email: String, password: String): Result<User> {
            return Result.success(User("u1", "Name", email, Role.OPERADOR))
        }

        override suspend fun register(
            nome: String,
            email: String,
            senha: String,
            empresa: String,
            codigoAcesso: String?
        ): Result<User> {
            lastCodigoAcesso = codigoAcesso
            return if (shouldReturnError) {
                Result.failure(Exception(errorMessage))
            } else {
                val role = if (codigoAcesso == "GEST2026") Role.GESTOR else Role.OPERADOR
                Result.success(User("u1", nome, email, role))
            }
        }

        override suspend fun logout() {}

        override fun getCurrentUser(): Flow<User?> = flowOf(null)
    }
}
