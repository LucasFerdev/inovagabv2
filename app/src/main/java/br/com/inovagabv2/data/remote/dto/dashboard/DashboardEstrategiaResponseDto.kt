package br.com.inovagabv2.data.remote.dto.dashboard

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import br.com.inovagabv2.data.remote.dto.strategy.RemoteStatusEstrategia
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class DashboardEstrategiaResponseDto(
    @SerialName("estrategiaId") val estrategiaId: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("status") val status: RemoteStatusEstrategia,
    @SerialName("quantidadeIdeias") val quantidadeIdeias: Long,
    @SerialName("ideiasAprovadas") val ideiasAprovadas: Long,
    @SerialName("quantidadeProjetos") val quantidadeProjetos: Long,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("investimento") val investimento: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("retorno") val retorno: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("lucro") val lucro: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("roiPercentual") val roiPercentual: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("progressoMedio") val progressoMedio: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("produtividadeMedia") val produtividadeMedia: BigDecimal,
    @SerialName("projetosAtrasados") val projetosAtrasados: Long
)
