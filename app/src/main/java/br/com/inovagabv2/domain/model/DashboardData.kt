package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    val roi: String,
    val profit: String,
    val costReduction: String,
    val productivity: String,
    val activeProjectsCount: Int,
    val approvedIdeasCount: Int,
    val resultsByProject: List<ProjectResult> = emptyList()
)

@Serializable
data class ProjectResult(
    val projectId: String,
    val projectName: String,
    val roi: String,
    val financialReturn: String,
    val investment: String,
    val impact: String
)
