package br.com.inovagabv2.presentation.manager.home

import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.User

data class ManagerHomeState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val ideas: List<Idea> = emptyList(),
    val metrics: ManagerMetrics = ManagerMetrics(),
    val error: String? = null
)

data class ManagerMetrics(
    val receivedIdeas: Int = 0,
    val inAnalysisIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val activeProjects: Int = 0
)
