package br.com.inovagabv2.domain.model

import java.math.BigDecimal

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val estrategiaId: String = "",
    val ideiaOrigemId: String? = null,
    val stage: ProjectStage = ProjectStage.PLANEJAMENTO,
    val status: ProjectStatus = ProjectStatus.PLANEJADO,
    val percentualProgresso: Int = 0,
    val investment: BigDecimal = BigDecimal.ZERO,
    val deadline: String = "",
    val retornoFinanceiro: BigDecimal? = null,
    val ganhoProdutividadePercentual: BigDecimal? = null,
    val resultado: String? = null,
    val gestorResponsavelId: String = "",
    val isAtrasado: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val version: Long? = null
) {
    val progress: Float get() = (percentualProgresso.coerceIn(0, 100) / 100f)
    val isReadOnly: Boolean get() = status == ProjectStatus.CONCLUIDO || status == ProjectStatus.CANCELADO
}
