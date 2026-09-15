package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.data.remote.dto.ranking.RankingColaboradorResponseDto
import br.com.inovagabv2.data.remote.dto.ranking.RemoteMedalhaRanking
import br.com.inovagabv2.domain.model.MedalhaRanking
import br.com.inovagabv2.domain.model.RankingColaborador

fun RemoteMedalhaRanking.toDomain(): MedalhaRanking = when (this) {
    RemoteMedalhaRanking.OURO -> MedalhaRanking.OURO
    RemoteMedalhaRanking.PRATA -> MedalhaRanking.PRATA
    RemoteMedalhaRanking.BRONZE -> MedalhaRanking.BRONZE
    RemoteMedalhaRanking.SEM_MEDALHA -> MedalhaRanking.SEM_MEDALHA
}

fun RankingColaboradorResponseDto.toDomain(): RankingColaborador {
    return RankingColaborador(
        posicao = posicao,
        nome = nome,
        empresa = empresa,
        ideiasAprovadas = ideiasAprovadas,
        ideiasImplementadas = ideiasImplementadas,
        medalha = medalha.toDomain()
    )
}
