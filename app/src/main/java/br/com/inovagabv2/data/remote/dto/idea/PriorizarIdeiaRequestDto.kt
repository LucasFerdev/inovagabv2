package br.com.inovagabv2.data.remote.dto.idea

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriorizarIdeiaRequestDto(
    @SerialName("prioridade") val prioridade: Int,
    @SerialName("justificativa") val justificativa: String? = null
)
