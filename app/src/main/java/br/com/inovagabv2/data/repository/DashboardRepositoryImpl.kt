package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.model.DashboardProjectDetails
import br.com.inovagabv2.domain.model.DashboardStrategyDetails
import br.com.inovagabv2.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val api: InovaGabApi,
    private val json: Json
) : DashboardRepository {

    override fun getDashboardData(): Flow<DashboardData> = flow {
        val response = api.consultarDashboardResumo()
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception(parseErrorMessage(response))
        }
    }

    override fun getDashboardStrategyDetails(strategyId: String): Flow<DashboardStrategyDetails?> = flow {
        val response = api.consultarDashboardEstrategia(strategyId)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception(parseErrorMessage(response))
        }
    }

    override fun getDashboardProjectDetails(projectId: String): Flow<DashboardProjectDetails?> = flow {
        val response = api.consultarDashboardProjeto(projectId)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception(parseErrorMessage(response))
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
            } catch (_: Exception) {}
        }
        return when (response.code()) {
            401 -> "Sua sessão expirou. Faça login novamente."
            403 -> "Acesso negado ao Dashboard."
            404 -> "Dados do Dashboard não encontrados."
            else -> "Serviço do Dashboard indisponível no momento."
        }
    }
}
