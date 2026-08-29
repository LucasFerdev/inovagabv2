package br.com.inovagabv2.data.repository

import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        // Mock authentication
        val user = when (email) {
            "operador@aguia.com" -> User("1", "João Silva", email, Role.OPERADOR)
            "gestor@aguia.com" -> User("2", "Mariana Costa", email, Role.GESTOR)
            "lideranca@aguia.com" -> User("3", "Carlos Mendes", email, Role.LIDERANCA)
            else -> return Result.failure(Exception("Usuário não encontrado"))
        }
        sessionManager.saveSession(user)
        return Result.success(user)
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override fun getCurrentUser(): Flow<User?> {
        return sessionManager.userSession
    }
}
