package br.com.inovagabv2.data.remote.dto.strategy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoricoEstrategiaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("acao") val acao: RemoteAcaoHistoricoEstrategia,
    @SerialName("dataHora") val dataHora: String,
    @SerialName("usuarioId") val usuarioId: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("descricao") val descricao: String,
    @SerialName("data") val data: String,
    @SerialName("categoria") val categoria: String,
    @SerialName("campanha") val campanha: String,
    @SerialName("status") val status: RemoteStatusEstrategia
)
