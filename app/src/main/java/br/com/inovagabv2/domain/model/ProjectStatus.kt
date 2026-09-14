package br.com.inovagabv2.domain.model

enum class ProjectStatus(val displayName: String) {
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    PAUSADO("Pausado"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado")
}

enum class ProjectStage(val displayName: String) {
    PLANEJAMENTO("Planejamento"),
    DESENVOLVIMENTO("Desenvolvimento"),
    PILOTO("Piloto"),
    IMPLEMENTACAO("Implementação"),
    ENCERRAMENTO("Encerramento")
}
