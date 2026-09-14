package br.com.inovagabv2.data.remote.dto.strategy

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteAcaoHistoricoEstrategia {
    CRIADA,
    ATUALIZADA,
    ATIVADA,
    DESATIVADA,
    ARQUIVADA
}
