package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.repository.AiRepository
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val api: InovaGabApi,
    private val json: Json
) : AiRepository {

    override suspend fun analyzeIdea(ideaId: String, recalculate: Boolean): Result<AiAnalysis> {
        return try {
            val response = api.analisarIdeiaIa(ideiaId = ideaId, recalcular = recalculate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIdeaAnalysis(ideaId: String): Result<AiAnalysis> {
        return try {
            val response = api.consultarAnaliseIa(ideiaId = ideaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
            400 -> "Solicitação de análise inválida."
            401 -> "Sessão inválida ou expirada."
            403 -> "Acesso negado para esta operação de IA."
            404 -> "Esta ideia ainda não possui análise da IA."
            409 -> "Operação incompatível com o estado atual da ideia."
            429 -> "O limite temporário da IA foi atingido. Tente novamente mais tarde."
            502, 503, 504 -> "O serviço de IA está indisponível no momento."
            else -> "Erro ao processar análise da IA (${response.code()})."
        }
    }
}
