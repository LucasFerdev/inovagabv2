package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.AiAnalysis

interface AiRepository {
    suspend fun analyzeIdea(ideaId: String, recalculate: Boolean = false): Result<AiAnalysis>
    suspend fun getIdeaAnalysis(ideaId: String): Result<AiAnalysis>
}
