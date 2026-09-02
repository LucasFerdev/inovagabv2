package br.com.inovagabv2.presentation.operator.home

import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.Strategy

data class OperatorHomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val sentCount: Int = 0,
    val inAnalysisCount: Int = 0,
    val approvedCount: Int = 0,
    val recentIdeas: List<Idea> = emptyList(),
    val strategies: List<Strategy> = emptyList(),
    val error: String? = null
)
