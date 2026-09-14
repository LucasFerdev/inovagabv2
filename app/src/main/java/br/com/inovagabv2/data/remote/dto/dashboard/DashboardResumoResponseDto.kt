package br.com.inovagabv2.data.remote.dto.dashboard

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import br.com.inovagabv2.data.remote.dto.idea.RemoteStatusIdeia
import br.com.inovagabv2.data.remote.dto.project.RemoteStatusProjeto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class DashboardResumoResponseDto(
    @SerialName("totalProjetos") val totalProjetos: Long,
    @SerialName("totalPlanejado") val totalPlanejado: Long,
    @SerialName("totalEmAndamento") val totalEmAndamento: Long,
    @SerialName("totalPausado") val totalPausado: Long,
    @SerialName("totalConcluido") val totalConcluido: Long,
    @SerialName("totalCancelado") val totalCancelado: Long,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("investimentoTotal") val investimentoTotal: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("retornoFinanceiroTotal") val retornoFinanceiroTotal: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("lucroObtido") val lucroObtido: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("roiPercentual") val roiPercentual: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("progressoMedio") val progressoMedio: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("ganhoMedioProdutividade") val ganhoMedioProdutividade: BigDecimal,
    @SerialName("projetosAtrasados") val projetosAtrasados: Long,
    @SerialName("ideiasEnviadas") val ideiasEnviadas: Long,
    @SerialName("ideiasEmAnalise") val ideiasEmAnalise: Long,
    @SerialName("ideiasAprovadas") val ideiasAprovadas: Long,
    @SerialName("ideiasRejeitadas") val ideiasRejeitadas: Long,
    @SerialName("projetosPorStatus") val projetosPorStatus: Map<RemoteStatusProjeto, Long> = emptyMap(),
    @SerialName("ideiasPorStatus") val ideiasPorStatus: Map<RemoteStatusIdeia, Long> = emptyMap()
)
