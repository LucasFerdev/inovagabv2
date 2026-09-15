package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.domain.model.RankingColaborador
import br.com.inovagabv2.domain.repository.RankingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RankingRepositoryImpl @Inject constructor(
    private val api: InovaGabApi,
    private val json: Json
) : RankingRepository {

    override fun getRankingColaboradores(): Flow<Result<List<RankingColaborador>>> = flow {
        val result = try {
            val response = api.getRankingColaboradores()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    private fun <T> parseErrorMessage(response: Response<T>): String {
        val errorBody = response.errorBody()?.string()
        if (!errorBody.isNullOrBlank()) {
            try {
                val apiError = json.decodeFromString<ApiErrorDto>(errorBody)
                if (!apiError.mensagem.isNullOrBlank()) {
                    return apiError.mensagem
                }
            } catch (_: Exception) {}
        }
        return when (response.code()) {
            401 -> "Sua sessão expirou. Faça login novamente."
            403 -> "Acesso negado ao ranking."
            else -> "Erro ao carregar o ranking de inovação."
        }
    }
}
