package br.com.inovagabv2.data.remote.dto.dashboard

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import br.com.inovagabv2.data.remote.dto.project.RemoteEtapaProjeto
import br.com.inovagabv2.data.remote.dto.project.RemoteStatusProjeto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class DashboardProjetoResponseDto(
    @SerialName("id") val id: String,
    @SerialName("nome") val nome: String,
    @SerialName("descricao") val descricao: String,
    @SerialName("estrategia") val estrategia: DashboardEstrategiaReferenciaResponseDto? = null,
    @SerialName("ideiaOrigem") val ideiaOrigem: DashboardIdeiaReferenciaResponseDto? = null,
    @SerialName("etapa") val etapa: RemoteEtapaProjeto,
    @SerialName("status") val status: RemoteStatusProjeto,
    @SerialName("percentualProgresso") val percentualProgresso: Int,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("investimento") val investimento: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("retornoFinanceiro") val retornoFinanceiro: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("lucro") val lucro: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("roiPercentual") val roiPercentual: BigDecimal? = null,
    @SerialName("prazo") val prazo: String,
    @SerialName("atrasado") val atrasado: Boolean,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("ganhoProdutividadePercentual") val ganhoProdutividadePercentual: BigDecimal? = null,
    @SerialName("resultado") val resultado: String? = null
)
