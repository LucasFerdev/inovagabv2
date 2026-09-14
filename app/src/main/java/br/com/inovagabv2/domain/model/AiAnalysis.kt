package br.com.inovagabv2.domain.model

data class AiAnalysis(
    val ideaId: String,
    val overallScore: Int,
    val suggestedPriority: Int,
    val executiveSummary: String,
    val strengths: List<String> = emptyList(),
    val risks: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val model: String,
    val generatedAt: String,
    val warning: String? = null
)
