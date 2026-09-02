package br.com.inovagabv2.presentation.operator.create

import br.com.inovagabv2.domain.model.Idea

data class CreateIdeaState(
    val currentStep: Int = 1,
    val category: String = "",
    val title: String = "",
    val description: String = "",
    val benefits: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val isStep1Valid: Boolean get() = category.isNotBlank()
    val isStep2Valid: Boolean get() = title.isNotBlank() && description.isNotBlank() && benefits.isNotBlank()
}
