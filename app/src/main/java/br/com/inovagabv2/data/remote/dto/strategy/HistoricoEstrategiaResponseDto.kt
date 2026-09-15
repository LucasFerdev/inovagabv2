package br.com.inovagabv2.data.remote.dto.strategy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoricoEstrategiaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("acao") val acao: String,
    @SerialName("dataHora") val dataHora: String,
    @SerialName("usuarioId") val usuarioId: String? = null,
    @SerialName("titulo") val titulo: String? = null,
    @SerialName("descricao") val descricao: String? = null,
    @SerialName("data") val data: String? = null,
    @SerialName("categoria") val categoria: String? = null,
    @SerialName("campanha") val campanha: String? = null,
    @SerialName("status") val status: String? = null
)
