package br.com.inovagabv2.data.remote.dto.strategy

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteStatusEstrategia {
    RASCUNHO,
    ATIVA,
    INATIVA,
    ARQUIVADA
}
