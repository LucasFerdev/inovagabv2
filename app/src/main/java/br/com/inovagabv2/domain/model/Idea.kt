package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Idea(
    val id: String,
    val title: String,
    val problem: String = "",
    val proposedSolution: String = "",
    val expectedBenefits: String = "",
    val category: String,
    val strategyId: String = "",
    val authorId: String = "",
    val authorName: String = "Autor não informado",
    val status: IdeaStatus = IdeaStatus.ENVIADA,
    val priority: Int? = null,
    val evaluationJustification: String? = null,
    val evaluatedById: String? = null,
    val evaluatedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val version: Long? = null,
    val timeline: List<TimelineEvent> = emptyList()
) {
    val description: String get() = problem.ifBlank { proposedSolution }
    val benefits: String get() = expectedBenefits
}
