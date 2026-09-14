package br.com.inovagabv2.data.remote.dto.idea

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteStatusIdeia {
    ENVIADA,
    EM_ANALISE,
    APROVADA,
    REJEITADA,
    ARQUIVADA
}
