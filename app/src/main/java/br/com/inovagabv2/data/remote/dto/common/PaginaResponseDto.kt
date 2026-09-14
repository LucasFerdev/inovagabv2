package br.com.inovagabv2.data.remote.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginaResponseDto<T>(
    @SerialName("conteudo") val conteudo: List<T>,
    @SerialName("pagina") val pagina: Int,
    @SerialName("tamanho") val tamanho: Int,
    @SerialName("totalElementos") val totalElementos: Long,
    @SerialName("totalPaginas") val totalPaginas: Int,
    @SerialName("primeira") val primeira: Boolean,
    @SerialName("ultima") val ultima: Boolean
)
