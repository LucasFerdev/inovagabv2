package br.com.inovagabv2.data.repository

import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.data.remote.dto.LoginRequestDto
import br.com.inovagabv2.data.remote.dto.RegisterRequestDto
import br.com.inovagabv2.data.remote.dto.toDomain
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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
                    sessionManager.saveSession(user = user, token = body.token)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Resposta do servidor vazia."))
                }
            } else {
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception(parseExceptionMessage(e)))
        }
    }

    override suspend fun register(nome: String, email: String, senha: String, empresa: String): Result<User> {
        return try {
            val request = RegisterRequestDto(
                nome = nome,
                email = email,
                senha = senha,
                empresa = empresa
            )
            val response = api.register(request)
            if (response.isSuccessful) {
                login(email = email, password = senha)
            } else {
                val errorMessage = parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception(parseExceptionMessage(e)))
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override fun getCurrentUser(): Flow<User?> {
        return sessionManager.userSession
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
        return when (e) {
            is ConnectException, is UnknownHostException ->
                "Falha ao conectar com o servidor. Verifique se o backend está ligado e se a API_BASE_URL está correta."
            is SocketTimeoutException ->
                "O tempo de resposta do servidor expirou. Verifique se o backend está ligado e se a API_BASE_URL está correta."
            is SerializationException ->
                "Erro ao processar a resposta do servidor."
            is IOException ->
                "Erro de rede ao se comunicar com o servidor. O backend pode estar desligado ou a API_BASE_URL incorreta."
            else ->
                e.message ?: "Ocorreu um erro inesperado."
        }
    }
}
