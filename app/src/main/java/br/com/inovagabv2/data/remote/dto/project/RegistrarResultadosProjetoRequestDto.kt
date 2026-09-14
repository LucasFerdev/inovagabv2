package br.com.inovagabv2.data.remote.dto.project

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class RegistrarResultadosProjetoRequestDto(
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("retornoFinanceiro") val retornoFinanceiro: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("ganhoProdutividadePercentual") val ganhoProdutividadePercentual: BigDecimal,
    @SerialName("resultado") val resultado: String
)
