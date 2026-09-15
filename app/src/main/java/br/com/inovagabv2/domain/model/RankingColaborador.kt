package br.com.inovagabv2.domain.model

enum class MedalhaRanking {
    OURO,
    PRATA,
    BRONZE,
    SEM_MEDALHA
}

data class RankingColaborador(
    val posicao: Int,
    val nome: String,
    val empresa: String,
    val ideiasAprovadas: Long,
    val ideiasImplementadas: Long,
    val medalha: MedalhaRanking
)
