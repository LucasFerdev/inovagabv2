package br.com.inovagabv2.data.remote.dto.ranking

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteMedalhaRanking {
    OURO,
    PRATA,
    BRONZE,
    SEM_MEDALHA
}
