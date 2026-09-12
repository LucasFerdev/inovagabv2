package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Idea(
    val id: String,
    val title: String,
    val description: String,
    val authorId: String,
    val authorName: String,
    val status: IdeaStatus,
    val priority: Priority? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val benefits: String,
    val category: String,
    val strategyId: String? = null,
    val attachments: List<String> = emptyList(),
    val timeline: List<TimelineEvent> = emptyList()
)
