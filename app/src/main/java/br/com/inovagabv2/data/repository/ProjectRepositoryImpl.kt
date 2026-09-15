package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.mapper.toRemote
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.ApiErrorDto
import br.com.inovagabv2.data.remote.dto.project.*
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val api: InovaGabApi,
    private val json: Json
) : ProjectRepository {

    override fun getProjects(): Flow<List<Project>> = flow {
        val response = api.listarProjetos(pagina = 0, tamanho = 100)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.conteudo.map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }

    override fun getProjectsRemote(
        status: ProjectStatus?,
        etapa: ProjectStage?,
        estrategiaId: String?,
        gestorId: String?,
        prazo: String?,
        pagina: Int,
        tamanho: Int
    ): Flow<Pagina<Project>> = flow {
        val response = api.listarProjetos(
            status = status?.toRemote(),
            etapa = etapa?.toRemote(),
            estrategiaId = estrategiaId,
            gestorId = gestorId,
            prazo = prazo,
            pagina = pagina,
            tamanho = tamanho
        )
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            throw Exception(parseErrorMessage(response))
        }
    }

    override fun getProjectById(id: String): Flow<Project?> = flow {
        val response = api.buscarProjetoPorId(id)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.toDomain())
        } else {
            emit(null)
        }
    }

    override fun consultarHistorico(id: String): Flow<List<br.com.inovagabv2.domain.model.HistoryItem>> = flow {
        val response = api.consultarHistoricoProjeto(id)
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!.map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }

    override suspend fun createProject(
        nome: String,
        descricao: String,
        estrategiaId: String,
        ideiaOrigemId: String?,
        investimento: BigDecimal,
        prazo: String
    ): Result<Project> {
        return try {
            val dto = CriarProjetoRequestDto(
                nome = nome,
                descricao = descricao,
                estrategiaId = estrategiaId,
                ideiaOrigemId = ideiaOrigemId,
                investimento = investimento,
                prazo = prazo
            )
            val response = api.criarProjeto(dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProject(project: Project): Result<Unit> {
        val res = createProject(
            nome = project.name,
            descricao = project.description,
            estrategiaId = project.estrategiaId,
            ideiaOrigemId = project.ideiaOrigemId,
            investimento = project.investment,
            prazo = project.deadline.ifBlank { "2026-12-31" }
        )
        return if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull()!!)
    }

    override suspend fun updateProject(
        id: String,
        nome: String,
        descricao: String,
        estrategiaId: String,
        ideiaOrigemId: String?,
        investimento: BigDecimal,
        prazo: String
    ): Result<Project> {
        return try {
            val dto = AtualizarProjetoRequestDto(
                nome = nome,
                descricao = descricao,
                estrategiaId = estrategiaId,
                ideiaOrigemId = ideiaOrigemId,
                investimento = investimento,
                prazo = prazo
            )
            val response = api.atualizarProjeto(id, dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProject(project: Project): Result<Unit> {
        val res = updateProject(
            id = project.id,
            nome = project.name,
            descricao = project.description,
            estrategiaId = project.estrategiaId,
            ideiaOrigemId = project.ideiaOrigemId,
            investimento = project.investment,
            prazo = project.deadline.ifBlank { "2026-12-31" }
        )
        return if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull()!!)
    }

    override suspend fun updateProgress(
        id: String,
        etapa: ProjectStage,
        status: ProjectStatus,
        percentualProgresso: Int,
        justificativa: String?
    ): Result<Project> {
        return try {
            val dto = AtualizarProgressoProjetoRequestDto(
                etapa = etapa.toRemote(),
                status = status.toRemote(),
                percentualProgresso = percentualProgresso,
                justificativa = justificativa
            )
            val response = api.atualizarProgressoProjeto(id, dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerResults(
        id: String,
        retornoFinanceiro: BigDecimal,
        ganhoProdutividadePercentual: BigDecimal,
        resultado: String
    ): Result<Project> {
        return try {
            val dto = RegistrarResultadosProjetoRequestDto(
                retornoFinanceiro = retornoFinanceiro,
                ganhoProdutividadePercentual = ganhoProdutividadePercentual,
                resultado = resultado
            )
            val response = api.registrarResultadosProjeto(id, dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun concludeProject(id: String): Result<Project> {
        return try {
            val response = api.concluirProjeto(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseErrorMessage(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelProject(id: String): Result<Unit> {
        return try {
            val response = api.cancelarProjeto(id)
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
            } catch (_: Exception) {}
        }
        return when (response.code()) {
            400 -> "Dados do projeto inválidos."
            401 -> "Sua sessão expirou. Faça login novamente."
            403 -> "Acesso negado para esta operação de projeto."
            404 -> "Projeto não encontrado."
            409 -> "Operação incompatível com o estado atual do projeto."
            else -> "Erro no servidor (${response.code()})."
        }
    }
}
