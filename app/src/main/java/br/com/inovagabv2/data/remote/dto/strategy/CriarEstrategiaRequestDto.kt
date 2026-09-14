package br.com.inovagabv2.data.remote.dto.strategy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CriarEstrategiaRequestDto(
    @SerialName("titulo") val titulo: String,
    @SerialName("descricao") val descricao: String,
    @SerialName("data") val data: String, // YYYY-MM-DD
    @SerialName("categoria") val categoria: String,
    @SerialName("campanha") val campanha: String
)
