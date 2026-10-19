package br.com.inovagabv2.data.repository

import android.util.Log
import br.com.inovagabv2.BuildConfig
import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.core.util.SERVER_STARTING_MESSAGE
import br.com.inovagabv2.core.util.isServerStartingTimeout
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.data.remote.dto.LoginRequestDto
import br.com.inovagabv2.data.remote.dto.RegisterRequestDto
import br.com.inovagabv2.data.remote.dto.toDomain
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: InovaGabApi,
    private val sessionManager: SessionManager,
    private val json: Json
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequestDto(email = email, senha = password)
            val response = api.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val user = body.usuario.toDomain()
                    sessionManager.saveSession(user = user, token = body.token, expiresInSeconds = body.expiresIn)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Resposta do servidor vazia."))
                }
            } else {
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            logError("AuthRepository", "Erro durante o login", e)
            Result.failure(Exception(parseExceptionMessage(e)))
        }
    }

    override suspend fun register(
        nome: String,
        email: String,
        senha: String,
        empresa: String,
        codigoAcesso: String?
    ): Result<User> {
        return try {
            val normalizedCodigo = codigoAcesso?.trim()?.ifBlank { null }
            val request = RegisterRequestDto(
                nome = nome,
                email = email,
                senha = senha,
                empresa = empresa,
                codigoAcesso = normalizedCodigo
            )
            val response = api.register(request)
            if (response.isSuccessful) {
                login(email = email, password = senha)
            } else {
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            logError("AuthRepository", "Erro durante o cadastro", e)
            Result.failure(Exception(parseExceptionMessage(e)))
        }
    }

    override suspend fun logout() {
        sessionManager.markManualLogout()
        try {
            sessionManager.clearSession()
        } catch (e: Exception) {
            sessionManager.clearManualLogout()
            throw e
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return sessionManager.userSession
    }

    override suspend fun refreshCurrentUser(): Result<Unit> {
        if (sessionManager.getToken().isNullOrBlank()) return Result.success(Unit)
        if (sessionManager.userSession.firstOrNull() == null) return Result.success(Unit)
        return try {
            val response = api.getMe()
            if (response.isSuccessful) {
                response.body()?.let { sessionManager.updateStoredUser(it.toDomain()) }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    private fun logError(tag: String, message: String, throwable: Throwable) {
        if (!BuildConfig.DEBUG) return
        try {
            Log.e(tag, message, throwable)
        } catch (_: Throwable) {
            // android.util.Log não existe nos testes unitários da JVM.
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String {
        val errorBody = response.errorBody()?.string()
        if (!errorBody.isNullOrBlank()) {
            try {
                val apiError = json.decodeFromString<ApiErrorDto>(errorBody)
                if (!apiError.mensagem.isNullOrBlank()) {
                    return apiError.mensagem
                }
            } catch (_: Exception) {
                // Ignore JSON parsing errors for error body
            }
        }
        return when (response.code()) {
            401 -> "E-mail ou senha incorretos."
            400 -> "Dados da requisição inválidos."
            404 -> "Serviço não encontrado."
            500 -> "Erro interno do servidor. Tente novamente mais tarde."
            else -> "Ocorreu um erro no servidor (${response.code()})."
        }
    }

    private fun parseExceptionMessage(e: Exception): String {
        if (e.isServerStartingTimeout()) return SERVER_STARTING_MESSAGE
        return when (e) {
            is SerializationException ->
                "Não foi possível processar a resposta do servidor. Tente novamente."
            is IOException ->
                "Não foi possível conectar ao servidor. Verifique sua internet e tente novamente."
            else ->
                "Ocorreu um erro inesperado."
        }
    }
}
