package br.com.inovagabv2.data.remote.dto.project

import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class CriarProjetoRequestDto(
    @SerialName("nome") val nome: String,
    @SerialName("descricao") val descricao: String,
    @SerialName("estrategiaId") val estrategiaId: String,
    @SerialName("ideiaOrigemId") val ideiaOrigemId: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName("investimento") val investimento: BigDecimal,
    @SerialName("prazo") val prazo: String
)
