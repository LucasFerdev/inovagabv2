package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.mapper.toRemote
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.data.remote.dto.idea.*
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.repository.IdeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdeaRepositoryImpl @Inject constructor(
    private val api: InovaGabApi,
    private val json: Json
) : IdeaRepository {

    override fun getIdeas(): Flow<List<Idea>> = flow {
        val response = api.listarIdeias(pagina = 0, tamanho = 100)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.conteudo.map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }

    override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flow {
        val response = api.listarMinhasIdeias(pagina = 0, tamanho = 100)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.conteudo.map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }

    override fun getIdeasRemote(
        status: IdeaStatus?,
        categoria: String?,
        estrategiaId: String?,
        prioridade: Int?,
        pagina: Int,
        tamanho: Int
    ): Flow<Pagina<Idea>> = flow {
        val response = api.listarIdeias(
            status = status?.toRemote(),
            categoria = categoria,
            estrategiaId = estrategiaId,
            prioridade = prioridade,
            pagina = pagina,
            tamanho = tamanho
        )
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception(parseErrorMessage(response))
        }
    }

    override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> = flow {
        val response = api.listarMinhasIdeias(pagina = pagina, tamanho = tamanho)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception(parseErrorMessage(response))
        }
    }

    override fun getIdeaById(id: String): Flow<Idea?> = flow {
        val response = api.buscarIdeiaPorId(id)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            emit(null)
        }
    }

    override suspend fun createIdea(
        titulo: String,
        problema: String,
        solucaoProposta: String,
        beneficiosEsperados: String,
        categoria: String,
        estrategiaId: String
    ): Result<Idea> {
        return try {
            val dto = CriarIdeiaRequestDto(
                titulo = titulo,
                problema = problema,
                solucaoProposta = solucaoProposta,
                beneficiosEsperados = beneficiosEsperados,
                categoria = categoria,
                estrategiaId = estrategiaId
            )
            val response = api.criarIdeia(dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createIdea(idea: Idea): Result<Unit> {
        val res = createIdea(
            titulo = idea.title,
            problema = idea.problem.ifBlank { idea.title },
            solucaoProposta = idea.proposedSolution.ifBlank { idea.title },
            beneficiosEsperados = idea.expectedBenefits.ifBlank { idea.title },
            categoria = idea.category,
            estrategiaId = idea.strategyId
        )
        return if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull()!!)
    }

    override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Int?, justificativa: String?): Result<Unit> {
        return try {
            val response = when (status) {
                IdeaStatus.EM_ANALISE -> api.analisarIdeia(id)
                IdeaStatus.APROVADA -> api.aprovarIdeia(id)
                IdeaStatus.REJEITADA -> api.rejeitarIdeia(id, RejeitarIdeiaRequestDto(justificativa ?: "Rejeitada"))
                else -> api.analisarIdeia(id)
            }
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analisar(id: String): Result<Idea> {
        return try {
            val response = api.analisarIdeia(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> {
        return try {
            val response = api.priorizarIdeia(id, PriorizarIdeiaRequestDto(prioridade, justificativa))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun aprovar(id: String): Result<Idea> {
        return try {
            val response = api.aprovarIdeia(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> {
        return try {
            val response = api.rejeitarIdeia(id, RejeitarIdeiaRequestDto(justificativa))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun arquivar(id: String): Result<Unit> {
        return try {
            val response = api.arquivarIdeia(id)
            if (response.isSuccessful) {
                Result.success(Unit)
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
            } catch (_: Exception) {
                // Ignore parsing exception
            }
        }
        return when (response.code()) {
            400 -> "Dados da requisição inválidos."
            401 -> "Sessão inválida ou expirada. Faça login novamente."
            403 -> "Acesso negado para esta operação."
            404 -> "Ideia não encontrada."
            409 -> "Status incompatível para esta operação."
            else -> "Erro no servidor (${response.code()})."
        }
    }
}
