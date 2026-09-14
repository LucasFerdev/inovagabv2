package br.com.inovagabv2.domain.model

import java.math.BigDecimal

data class DashboardData(
    val totalProjetos: Long = 0,
    val totalPlanejado: Long = 0,
    val totalEmAndamento: Long = 0,
    val totalPausado: Long = 0,
    val totalConcluido: Long = 0,
    val totalCancelado: Long = 0,
    val investimentoTotal: BigDecimal = BigDecimal.ZERO,
    val retornoFinanceiroTotal: BigDecimal = BigDecimal.ZERO,
    val lucroObtido: BigDecimal = BigDecimal.ZERO,
    val roiPercentual: BigDecimal = BigDecimal.ZERO,
    val progressoMedio: BigDecimal = BigDecimal.ZERO,
    val ganhoMedioProdutividade: BigDecimal = BigDecimal.ZERO,
    val projetosAtrasados: Long = 0,
    val ideiasEnviadas: Long = 0,
    val ideiasEmAnalise: Long = 0,
    val ideiasAprovadas: Long = 0,
    val ideiasRejeitadas: Long = 0,
    val projetosPorStatus: Map<ProjectStatus, Long> = emptyMap(),
    val ideiasPorStatus: Map<IdeaStatus, Long> = emptyMap(),
    val resultsByProject: List<ProjectResult> = emptyList()
) {
    val roi: String get() = String.format(java.util.Locale.US, "%.1f%%", roiPercentual.toDouble()).replace(".", ",")
    val profit: String get() = formatMonetary(lucroObtido)
    val investment: Double get() = investimentoTotal.toDouble()
    val financialReturn: Double get() = retornoFinanceiroTotal.toDouble()
    val activeProjectsCount: Int get() = (totalEmAndamento + totalPlanejado).toInt()
    val delayedProjectsCount: Int get() = projetosAtrasados.toInt()
    val approvedIdeasCount: Int get() = ideiasAprovadas.toInt()
    val inAnalysisIdeasCount: Int get() = ideiasEmAnalise.toInt()

    private fun formatMonetary(value: BigDecimal): String {
        val d = value.toDouble()
        return when {
            d >= 1_000_000 -> String.format(java.util.Locale.US, "R$ %.2f mi", d / 1_000_000).replace(".", ",")
            d >= 1_000 -> String.format(java.util.Locale.US, "R$ %.0f mil", d / 1_000)
            else -> String.format(java.util.Locale.US, "R$ %.2f", d).replace(".", ",")
        }
    }
}

data class ProjectResult(
    val projectId: String,
    val projectName: String,
    val roi: String,
    val financialReturn: String,
    val investment: String,
    val impact: String
)

data class DashboardStrategyDetails(
    val estrategiaId: String,
    val titulo: String,
    val status: StrategyStatus,
    val quantidadeIdeias: Long,
    val ideiasAprovadas: Long,
    val quantidadeProjetos: Long,
    val investimento: BigDecimal,
    val retorno: BigDecimal,
    val lucro: BigDecimal,
    val roiPercentual: BigDecimal,
    val progressoMedio: BigDecimal,
    val produtividadeMedia: BigDecimal,
    val projetosAtrasados: Long
)

data class DashboardProjectDetails(
    val id: String,
    val nome: String,
    val descricao: String,
    val estrategiaTitulo: String? = null,
    val ideiaOrigemTitulo: String? = null,
    val etapa: ProjectStage,
    val status: ProjectStatus,
    val percentualProgresso: Int,
    val investimento: BigDecimal,
    val retornoFinanceiro: BigDecimal? = null,
    val lucro: BigDecimal? = null,
    val roiPercentual: BigDecimal? = null,
    val prazo: String,
    val atrasado: Boolean,
    val ganhoProdutividadePercentual: BigDecimal? = null,
    val resultado: String? = null
)
