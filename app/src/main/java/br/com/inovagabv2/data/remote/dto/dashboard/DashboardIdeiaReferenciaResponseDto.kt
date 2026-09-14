package br.com.inovagabv2.data.remote.dto.dashboard

import br.com.inovagabv2.data.remote.dto.idea.RemoteStatusIdeia
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardIdeiaReferenciaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("status") val status: RemoteStatusIdeia
)
