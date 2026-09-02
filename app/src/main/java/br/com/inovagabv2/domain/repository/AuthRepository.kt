package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout()
    fun getCurrentUser(): Flow<User?>
}
