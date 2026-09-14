package br.com.inovagabv2.data.remote.dto.project

import kotlinx.serialization.Serializable

@Serializable
enum class RemoteEtapaProjeto {
    PLANEJAMENTO,
    DESENVOLVIMENTO,
    PILOTO,
    IMPLEMENTACAO,
    ENCERRAMENTO
}
