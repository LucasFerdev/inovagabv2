package br.com.inovagabv2.data.remote.dto.idea

import br.com.inovagabv2.data.remote.dto.ai.AnaliseIaIdeiaResponseDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoricoIdeiaResponseDto(
    @SerialName("id") val id: String,
    @SerialName("acao") val acao: String,
    @SerialName("dataHora") val dataHora: String,
    @SerialName("usuarioId") val usuarioId: String? = null,
    @SerialName("titulo") val titulo: String? = null,
    @SerialName("problema") val problema: String? = null,
    @SerialName("solucaoProposta") val solucaoProposta: String? = null,
    @SerialName("beneficiosEsperados") val beneficiosEsperados: String? = null,
    @SerialName("categoria") val categoria: String? = null,
    @SerialName("estrategiaId") val estrategiaId: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("prioridade") val prioridade: Int? = null,
    @SerialName("justificativaAvaliacao") val justificativaAvaliacao: String? = null,
    @SerialName("analiseIa") val analiseIa: AnaliseIaIdeiaResponseDto? = null
)
