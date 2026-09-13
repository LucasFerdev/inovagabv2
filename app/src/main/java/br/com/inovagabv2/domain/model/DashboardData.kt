package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    val roi: String = "0,0%",
    val profit: String = "R$ 0,00",
    val costReduction: String = "R$ 0,00",
    val productivity: String = "0%",
    val activeProjectsCount: Int = 0,
    val delayedProjectsCount: Int = 0,
    val approvedIdeasCount: Int = 0,
    val inAnalysisIdeasCount: Int = 0,
    val investment: Double = 0.0,
    val financialReturn: Double = 0.0,
    val roiPercentage: Double = 0.0,
    val projectsByStatus: Map<ProjectStatus, Int> = emptyMap(),
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
