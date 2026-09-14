package br.com.inovagabv2.data.remote.dto.project

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteStatusProjeto {
    PLANEJADO,
    EM_ANDAMENTO,
    PAUSADO,
    CONCLUIDO,
    CANCELADO
}
