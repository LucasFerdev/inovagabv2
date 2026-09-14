package br.com.inovagabv2.data.remote.dto.idea

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdeiaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("problema") val problema: String,
    @SerialName("solucaoProposta") val solucaoProposta: String,
    @SerialName("beneficiosEsperados") val beneficiosEsperados: String,
    @SerialName("categoria") val categoria: String,
    @SerialName("estrategiaId") val estrategiaId: String,
    @SerialName("autorId") val autorId: String,
    @SerialName("status") val status: RemoteStatusIdeia,
    @SerialName("prioridade") val prioridade: Int? = null,
    @SerialName("justificativaAvaliacao") val justificativaAvaliacao: String? = null,
    @SerialName("avaliadoPorId") val avaliadoPorId: String? = null,
    @SerialName("avaliadoEm") val avaliadoEm: String? = null,
    @SerialName("criadoEm") val criadoEm: String? = null,
    @SerialName("atualizadoEm") val atualizadoEm: String? = null,
    @SerialName("versao") val versao: Long? = null
)
