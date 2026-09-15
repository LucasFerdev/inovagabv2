package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.mapper.toRemote
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.data.remote.dto.strategy.AtualizarEstrategiaRequestDto
import br.com.inovagabv2.data.remote.dto.strategy.CriarEstrategiaRequestDto
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.StrategyStatus
import br.com.inovagabv2.domain.repository.StrategyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StrategyRepositoryImpl @Inject constructor(
    private val api: InovaGabApi,
    private val json: Json
) : StrategyRepository {

    override fun getStrategies(): Flow<List<Strategy>> = flow {
        val response = api.listarEstrategias(pagina = 0, tamanho = 100)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.conteudo.map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }

    override fun getStrategiesRemote(
        status: StrategyStatus?,
        categoria: String?,
        campanha: String?,
        pagina: Int,
        tamanho: Int
    ): Flow<Pagina<Strategy>> = flow {
        val response = api.listarEstrategias(
            status = status?.toRemote(),
            categoria = categoria,
            campanha = campanha,
            pagina = pagina,
            tamanho = tamanho
        )
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception(parseErrorMessage(response))
        }
    }

    override fun getActiveStrategies(): Flow<List<Strategy>> = flow {
        val response = api.listarEstrategiasAtivas()
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }

    override fun getStrategyById(id: String): Flow<Strategy?> = flow {
        val response = api.buscarEstrategiaPorId(id)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            emit(null)
        }
    }

    override fun consultarHistorico(id: String): Flow<List<br.com.inovagabv2.domain.model.HistoryItem>> = flow {
        val response = api.consultarHistoricoEstrategia(id)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }

    override suspend fun createStrategy(
        titulo: String,
        descricao: String,
        data: String,
        categoria: String,
        campanha: String
    ): Result<Strategy> {
        return try {
            val dto = CriarEstrategiaRequestDto(
                titulo = titulo,
                descricao = descricao,
                data = data,
                categoria = categoria,
                campanha = campanha
            )
            val response = api.criarEstrategia(dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createStrategy(strategy: Strategy): Result<Unit> {
        val res = createStrategy(
            titulo = strategy.title,
            descricao = strategy.description,
            data = strategy.date.ifBlank { "2026-09-15" },
            categoria = strategy.category ?: "Geral",
            campanha = strategy.campaign ?: "Geral"
        )
        return if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull()!!)
    }

    override suspend fun updateStrategy(
        id: String,
        titulo: String,
        descricao: String,
        data: String,
        categoria: String,
        campanha: String
    ): Result<Strategy> {
        return try {
            val dto = AtualizarEstrategiaRequestDto(
                titulo = titulo,
                descricao = descricao,
                data = data,
                categoria = categoria,
                campanha = campanha
            )
            val response = api.atualizarEstrategia(id, dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStrategy(strategy: Strategy): Result<Unit> {
        val res = updateStrategy(
            id = strategy.id,
            titulo = strategy.title,
            descricao = strategy.description,
            data = strategy.date.ifBlank { "2026-09-15" },
            categoria = strategy.category ?: "Geral",
            campanha = strategy.campaign ?: "Geral"
        )
        return if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull()!!)
    }

    override suspend fun activateStrategy(id: String): Result<Strategy> {
        return try {
            val response = api.ativarEstrategia(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deactivateStrategy(id: String): Result<Strategy> {
        return try {
            val response = api.desativarEstrategia(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun archiveStrategy(id: String): Result<Unit> {
        return try {
            val response = api.arquivarEstrategia(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteStrategy(id: String): Result<Unit> {
        return archiveStrategy(id)
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
                // Ignore parsing exception
            }
        }
        return when (response.code()) {
            400 -> "Dados da requisição inválidos."
            401 -> "Sessão inválida ou expirada. Faça login novamente."
            403 -> "Acesso negado para esta operação."
            404 -> "Estratégia não encontrada."
            409 -> "Status incompatível para esta operação."
            else -> "Erro no servidor (${response.code()})."
        }
    }
}
