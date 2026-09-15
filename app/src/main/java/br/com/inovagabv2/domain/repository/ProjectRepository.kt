package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>
    fun getProjectsRemote(
        status: ProjectStatus? = null,
        etapa: ProjectStage? = null,
        estrategiaId: String? = null,
        gestorId: String? = null,
        prazo: String? = null,
        pagina: Int = 0,
        tamanho: Int = 20
    ): Flow<Pagina<Project>>
    fun getProjectById(id: String): Flow<Project?>
    fun consultarHistorico(id: String): Flow<List<HistoryItem>>
    suspend fun createProject(
        nome: String,
        descricao: String,
        estrategiaId: String,
        ideiaOrigemId: String? = null,
        investimento: BigDecimal,
        prazo: String
    ): Result<Project>
    suspend fun createProject(project: Project): Result<Unit>
    suspend fun updateProject(
        id: String,
        nome: String,
        descricao: String,
        estrategiaId: String,
        ideiaOrigemId: String? = null,
        investimento: BigDecimal,
        prazo: String
    ): Result<Project>
    suspend fun updateProject(project: Project): Result<Unit>
    suspend fun updateProgress(
        id: String,
        etapa: ProjectStage,
        status: ProjectStatus,
        percentualProgresso: Int,
        justificativa: String? = null
    ): Result<Project>
    suspend fun registerResults(
        id: String,
        retornoFinanceiro: BigDecimal,
        ganhoProdutividadePercentual: BigDecimal,
        resultado: String
    ): Result<Project>
    suspend fun concludeProject(id: String): Result<Project>
    suspend fun cancelProject(id: String): Result<Unit>
}
