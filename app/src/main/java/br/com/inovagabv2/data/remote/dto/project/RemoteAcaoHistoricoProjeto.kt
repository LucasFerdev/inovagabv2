package br.com.inovagabv2.data.remote.dto.project

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteAcaoHistoricoProjeto {
    CRIADO,
    ATUALIZADO,
    PROGRESSO_ATUALIZADO,
    RESULTADOS_REGISTRADOS,
    CONCLUIDO,
    CANCELADO
}
