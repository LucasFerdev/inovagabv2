package br.com.inovagabv2.data.remote.dto.ranking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingColaboradorResponseDto(
    @SerialName("posicao") val posicao: Int,
    @SerialName("nome") val nome: String,
    @SerialName("empresa") val empresa: String,
    @SerialName("ideiasAprovadas") val ideiasAprovadas: Long,
    @SerialName("ideiasImplementadas") val ideiasImplementadas: Long,
    @SerialName("medalha") val medalha: RemoteMedalhaRanking
)
