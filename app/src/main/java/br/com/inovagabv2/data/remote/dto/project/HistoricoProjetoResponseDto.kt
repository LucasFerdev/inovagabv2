package br.com.inovagabv2.data.remote.dto.project

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class HistoricoProjetoResponseDto(
    @SerialName("id") val id: String,
    @SerialName("acao") val acao: RemoteAcaoHistoricoProjeto,
    @SerialName("dataHora") val dataHora: String,
    @SerialName("usuarioId") val usuarioId: String,
    @SerialName("nome") val nome: String,
    @SerialName("descricao") val descricao: String,
    @SerialName("estrategiaId") val estrategiaId: String,
    @SerialName("ideiaOrigemId") val ideiaOrigemId: String? = null,
    @SerialName("etapa") val etapa: RemoteEtapaProjeto,
    @SerialName("status") val status: RemoteStatusProjeto,
    @SerialName("percentualProgresso") val percentualProgresso: Int,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("investimento") val investimento: BigDecimal,
    @SerialName("prazo") val prazo: String,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("retornoFinanceiro") val retornoFinanceiro: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("ganhoProdutividadePercentual") val ganhoProdutividadePercentual: BigDecimal? = null,
    @SerialName("resultado") val resultado: String? = null,
    @SerialName("justificativa") val justificativa: String? = null
)
