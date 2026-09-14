package br.com.inovagabv2.data.remote.dto.idea

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteAcaoHistoricoIdeia {
    CRIADA,
    ATUALIZADA,
    ANALISADA,
    PRIORIZADA,
    APROVADA,
    REJEITADA,
    ARQUIVADA,
    ANALISADA_IA
}
