package br.com.inovagabv2.data.remote.dto.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AtualizarProgressoProjetoRequestDto(
    @SerialName("etapa") val etapa: RemoteEtapaProjeto,
    @SerialName("status") val status: RemoteStatusProjeto,
    @SerialName("percentualProgresso") val percentualProgresso: Int,
    @SerialName("justificativa") val justificativa: String? = null
)
