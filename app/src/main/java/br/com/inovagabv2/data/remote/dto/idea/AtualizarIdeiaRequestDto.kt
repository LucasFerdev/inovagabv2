package br.com.inovagabv2.data.remote.dto.idea

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AtualizarIdeiaRequestDto(
    @SerialName("titulo") val titulo: String,
    @SerialName("problema") val problema: String,
    @SerialName("solucaoProposta") val solucaoProposta: String,
    @SerialName("beneficiosEsperados") val beneficiosEsperados: String,
    @SerialName("categoria") val categoria: String,
    @SerialName("estrategiaId") val estrategiaId: String
)
