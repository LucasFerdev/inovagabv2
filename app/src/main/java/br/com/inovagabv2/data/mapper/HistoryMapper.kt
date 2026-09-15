package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.data.remote.dto.idea.HistoricoIdeiaResponseDto
import br.com.inovagabv2.data.remote.dto.project.HistoricoProjetoResponseDto
import br.com.inovagabv2.data.remote.dto.strategy.HistoricoEstrategiaResponseDto
import br.com.inovagabv2.domain.model.HistoryItem

fun formatActionName(actionStr: String): String {
    val clean = actionStr.uppercase().trim()
    return when (clean) {
        "ANALISADA_POR_IA", "ANALISADA_IA" -> "Analisada por IA"
        "CRIADA" -> "Criada"
        "CRIADO" -> "Criado"
        "ATUALIZADA" -> "Atualizada"
        "ATUALIZADO" -> "Atualizado"
        "ANALISADA" -> "Analisada"
        "PRIORIZADA" -> "Priorizada"
        "APROVADA" -> "Aprovada"
        "REJEITADA" -> "Rejeitada"
        "ATIVADA" -> "Ativada"
        "DESATIVADA" -> "Desativada"
        "ARQUIVADA" -> "Arquivada"
        "ARQUIVADO" -> "Arquivado"
        "PROGRESSO_ATUALIZADO" -> "Progresso atualizado"
        "RESULTADOS_REGISTRADOS" -> "Resultados registrados"
        "CONCLUIDO", "CONCLUÍDO" -> "Concluído"
        "CANCELADO" -> "Cancelado"
        else -> clean.replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun formatStatusName(statusStr: String?): String? {
    if (statusStr.isNullOrBlank()) return null
    val clean = statusStr.uppercase().trim()
    return when (clean) {
        "ENVIADA" -> "Enviada"
        "EM_ANALISE", "EM_ANÁLISE" -> "Em análise"
        "APROVADA" -> "Aprovada"
        "REJEITADA" -> "Rejeitada"
        "ARQUIVADA" -> "Arquivada"
        "RASCUNHO" -> "Rascunho"
        "ATIVA" -> "Ativa"
        "INATIVA" -> "Inativa"
        "PLANEJADO" -> "Planejado"
        "EM_ANDAMENTO" -> "Em andamento"
        "PAUSADO" -> "Pausado"
        "CONCLUIDO", "CONCLUÍDO" -> "Concluído"
        "CANCELADO" -> "Cancelado"
        else -> clean.replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun HistoricoIdeiaResponseDto.toDomain(): HistoryItem {
    return HistoryItem(
        id = id,
        action = formatActionName(acao),
        dateTime = dataHora,
        justification = justificativaAvaliacao,
        status = formatStatusName(status)
    )
}

fun HistoricoEstrategiaResponseDto.toDomain(): HistoryItem {
    return HistoryItem(
        id = id,
        action = formatActionName(acao),
        dateTime = dataHora,
        status = formatStatusName(status)
    )
}

fun HistoricoProjetoResponseDto.toDomain(): HistoryItem {
    return HistoryItem(
        id = id,
        action = formatActionName(acao),
        dateTime = dataHora,
        justification = justificativa,
        status = formatStatusName(status)
    )
}
