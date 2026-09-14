package br.com.inovagabv2.presentation.operator.create

import br.com.inovagabv2.domain.model.Strategy

data class CreateIdeaState(
    val currentStep: Int = 1,
    val activeStrategies: List<Strategy> = emptyList(),
    val selectedStrategy: Strategy? = null,
    val category: String = "",
    val title: String = "",
    val problem: String = "",
    val proposedSolution: String = "",
    val expectedBenefits: String = "",
    val isLoadingStrategies: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val isStep1Valid: Boolean get() = selectedStrategy != null && category.isNotBlank()
    val isStep2Valid: Boolean get() = title.isNotBlank() && (problem.isNotBlank() || proposedSolution.isNotBlank()) && expectedBenefits.isNotBlank()
}
