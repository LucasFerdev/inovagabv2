package br.com.inovagabv2.data.remote.dto.idea

import br.com.inovagabv2.data.remote.dto.ai.AnaliseIaIdeiaResponseDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoricoIdeiaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("acao") val acao: RemoteAcaoHistoricoIdeia,
    @SerialName("dataHora") val dataHora: String,
    @SerialName("usuarioId") val usuarioId: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("problema") val problema: String,
    @SerialName("solucaoProposta") val solucaoProposta: String,
    @SerialName("beneficiosEsperados") val beneficiosEsperados: String,
    @SerialName("categoria") val categoria: String,
    @SerialName("estrategiaId") val estrategiaId: String,
    @SerialName("status") val status: RemoteStatusIdeia,
    @SerialName("prioridade") val prioridade: Int? = null,
    @SerialName("justificativaAvaliacao") val justificativaAvaliacao: String? = null,
    @SerialName("analiseIa") val analiseIa: AnaliseIaIdeiaResponseDto? = null
)
