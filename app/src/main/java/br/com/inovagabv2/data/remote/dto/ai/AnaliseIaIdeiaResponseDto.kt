package br.com.inovagabv2.data.remote.dto.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnaliseIaIdeiaResponseDto(
    @SerialName("ideiaId") val ideiaId: String,
    @SerialName("pontuacaoGeral") val pontuacaoGeral: Int,
    @SerialName("prioridadeSugerida") val prioridadeSugerida: Int,
    @SerialName("resumoExecutivo") val resumoExecutivo: String,
    @SerialName("pontosFortes") val pontosFortes: List<String> = emptyList(),
    @SerialName("riscos") val riscos: List<String> = emptyList(),
    @SerialName("recomendacoes") val recomendacoes: List<String> = emptyList(),
    @SerialName("modelo") val modelo: String,
    @SerialName("geradoEm") val geradoEm: String,
    @SerialName("aviso") val aviso: String? = null
)
