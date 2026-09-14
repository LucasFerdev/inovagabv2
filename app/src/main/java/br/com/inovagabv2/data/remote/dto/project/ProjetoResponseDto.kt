package br.com.inovagabv2.data.remote.dto.project

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class ProjetoResponseDto(
    @SerialName("id") val id: String,
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
    @SerialName("gestorResponsavelId") val gestorResponsavelId: String,
    @SerialName("criadoEm") val criadoEm: String? = null,
    @SerialName("atualizadoEm") val atualizadoEm: String? = null,
    @SerialName("versao") val versao: Long? = null
)
