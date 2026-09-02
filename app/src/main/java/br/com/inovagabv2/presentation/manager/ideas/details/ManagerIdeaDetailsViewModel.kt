package br.com.inovagabv2.presentation.manager.ideas.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Priority
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerIdeaDetailsViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ideaId: String = checkNotNull(savedStateHandle["ideaId"])

    private val _state = MutableStateFlow(ManagerIdeaDetailsState())
    val state: StateFlow<ManagerIdeaDetailsState> = _state.asStateFlow()

    init {
        loadIdea()
    }

    private fun loadIdea() {
        viewModelScope.launch {
            ideaRepository.getIdeaById(ideaId).collect { idea ->
                _state.update { it.copy(idea = idea, isLoading = false) }
            }
        }
    }

    fun onPrioritySelected(priority: Priority) {
        _state.update { it.copy(selectedPriority = priority) }
    }

    fun onDecision(status: IdeaStatus) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            val result = ideaRepository.updateIdeaStatus(ideaId, status, _state.value.selectedPriority)
            if (result.isSuccess) {
                _state.update { it.copy(isSubmitting = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSubmitting = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}

data class ManagerIdeaDetailsState(
    val idea: Idea? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val selectedPriority: Priority? = null,
    val error: String? = null
)
