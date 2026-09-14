package br.com.inovagabv2.presentation.operator.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateIdeaViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val strategyRepository: StrategyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateIdeaState())
    val state: StateFlow<CreateIdeaState> = _state.asStateFlow()

    init {
        loadActiveStrategies()
    }

    fun loadActiveStrategies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingStrategies = true) }
            strategyRepository.getActiveStrategies().collect { strategies ->
                val activeOnly = strategies.filter { it.isPublished }
                _state.update {
                    it.copy(
                        activeStrategies = activeOnly,
                        selectedStrategy = activeOnly.firstOrNull(),
                        isLoadingStrategies = false
                    )
                }
            }
        }
    }

    fun onStrategySelected(strategy: Strategy) {
        _state.update { it.copy(selectedStrategy = strategy) }
    }

    fun onCategoryChange(category: String) {
        _state.update { it.copy(category = category) }
    }

    fun onTitleChange(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun onProblemChange(problem: String) {
        _state.update { it.copy(problem = problem) }
    }

    fun onProposedSolutionChange(solution: String) {
        _state.update { it.copy(proposedSolution = solution) }
    }

    fun onExpectedBenefitsChange(benefits: String) {
        _state.update { it.copy(expectedBenefits = benefits) }
    }

    fun nextStep() {
        if (_state.value.currentStep < 3) {
            _state.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_state.value.currentStep > 1) {
            _state.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun submitIdea() {
        val currentState = _state.value
        if (currentState.isLoading || currentState.selectedStrategy == null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val problemText = currentState.problem.ifBlank { currentState.proposedSolution }
            val solutionText = currentState.proposedSolution.ifBlank { currentState.problem }
            val benefitsText = currentState.expectedBenefits.ifBlank { "Benefícios operacionais esperados" }

            val result = ideaRepository.createIdea(
                titulo = currentState.title,
                problema = problemText,
                solucaoProposta = solutionText,
                beneficiosEsperados = benefitsText,
                categoria = currentState.category,
                estrategiaId = currentState.selectedStrategy.id
            )

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Erro ao criar ideia"
                    )
                }
            }
        }
    }
}
