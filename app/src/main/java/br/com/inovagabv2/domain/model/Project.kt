package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val name: String,
    val description: String,
    val status: ProjectStatus,
    val progress: Float, // 0.0 to 1.0
    val startDate: String,
    val deadline: String,
    val investment: String,
    val authorName: String? = null,
    val results: List<ProjectMetric> = emptyList(),
    val nextSteps: List<String> = emptyList()
)
