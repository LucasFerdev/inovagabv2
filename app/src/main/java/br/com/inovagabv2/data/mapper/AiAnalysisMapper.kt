package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.data.remote.dto.ai.AnaliseIaIdeiaResponseDto
import br.com.inovagabv2.domain.model.AiAnalysis

fun AnaliseIaIdeiaResponseDto.toDomain(): AiAnalysis {
    return AiAnalysis(
        ideaId = ideiaId,
        overallScore = pontuacaoGeral,
        suggestedPriority = prioridadeSugerida,
        executiveSummary = resumoExecutivo,
        strengths = pontosFortes,
        risks = riscos,
        recommendations = recomendacoes,
        model = modelo,
        generatedAt = geradoEm,
        warning = aviso
    )
}
