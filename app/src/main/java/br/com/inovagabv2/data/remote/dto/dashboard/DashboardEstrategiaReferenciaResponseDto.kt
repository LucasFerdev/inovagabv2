package br.com.inovagabv2.data.remote.dto.dashboard

import br.com.inovagabv2.data.remote.dto.strategy.RemoteStatusEstrategia
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardEstrategiaReferenciaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("status") val status: RemoteStatusEstrategia
)
