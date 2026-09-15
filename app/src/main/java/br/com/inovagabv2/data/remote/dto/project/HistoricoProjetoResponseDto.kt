package br.com.inovagabv2.data.remote.dto.project

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class HistoricoProjetoResponseDto(
    @SerialName("id") val id: String,
    @SerialName("acao") val acao: String,
    @SerialName("dataHora") val dataHora: String,
    @SerialName("usuarioId") val usuarioId: String? = null,
    @SerialName("nome") val nome: String? = null,
    @SerialName("descricao") val descricao: String? = null,
    @SerialName("estrategiaId") val estrategiaId: String? = null,
    @SerialName("ideiaOrigemId") val ideiaOrigemId: String? = null,
    @SerialName("etapa") val etapa: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("percentualProgresso") val percentualProgresso: Int? = null,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("investimento") val investimento: BigDecimal? = null,
    @SerialName("prazo") val prazo: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("retornoFinanceiro") val retornoFinanceiro: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("ganhoProdutividadePercentual") val ganhoProdutividadePercentual: BigDecimal? = null,
    @SerialName("resultado") val resultado: String? = null,
    @SerialName("justificativa") val justificativa: String? = null
)
