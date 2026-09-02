package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateStrategyViewModel @Inject constructor(
    private val repository: StrategyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateStrategyState())
    val state = _state.asStateFlow()

    fun onTitleChange(title: String) = _state.update { it.copy(title = title) }
    fun onDescriptionChange(desc: String) = _state.update { it.copy(description = desc) }
    fun addObjective(obj: String) = _state.update { it.copy(objectives = it.objectives + obj) }
    
    fun onNextStep() {
        if (_state.value.currentStep < 2) {
            _state.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun onPreviousStep() {
        if (_state.value.currentStep > 0) {
            _state.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun saveStrategy(isPublished: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val strategy = Strategy(
                id = UUID.randomUUID().toString(),
                title = _state.value.title,
                description = _state.value.description,
                objectives = _state.value.objectives,
                isPublished = isPublished,
                createdAt = "15/08/2026",
                updatedAt = "15/08/2026"
            )
            repository.createStrategy(strategy).onSuccess {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}

data class CreateStrategyState(
    val currentStep: Int = 0,
    val title: String = "",
    val description: String = "",
    val objectives: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
