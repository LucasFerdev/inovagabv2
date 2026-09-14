package br.com.inovagabv2.data.remote.dto.strategy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EstrategiaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("descricao") val descricao: String,
    @SerialName("data") val data: String,
    @SerialName("categoria") val categoria: String,
    @SerialName("campanha") val campanha: String,
    @SerialName("status") val status: RemoteStatusEstrategia,
    @SerialName("criadoPorId") val criadoPorId: String,
    @SerialName("atualizadoPorId") val atualizadoPorId: String,
    @SerialName("criadoEm") val criadoEm: String? = null,
    @SerialName("atualizadoEm") val atualizadoEm: String? = null,
    @SerialName("versao") val versao: Long? = null
)
