package br.com.inovagabv2.presentation.operator.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateIdeaViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(CreateIdeaState())
    val state: StateFlow<CreateIdeaState> = _state.asStateFlow()

    fun onCategoryChange(category: String) {
        _state.update { it.copy(category = category) }
    }

    fun onTitleChange(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun onBenefitsChange(benefits: String) {
        _state.update { it.copy(benefits = benefits) }
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
        viewModelScope.launch {
            val currentState = _state.value
            _state.update { it.copy(isLoading = true) }
            
            val user = sessionManager.userSession.first()
            if (user == null) {
                _state.update { it.copy(isLoading = false, error = "Usuário não autenticado") }
                return@launch
            }

            val newIdea = Idea(
                id = UUID.randomUUID().toString(),
                title = currentState.title,
                description = currentState.description,
                authorId = user.id,
                authorName = user.name,
                status = IdeaStatus.ENVIADA,
                createdAt = "15/08/2026", // Simplified for mock
                benefits = currentState.benefits,
                category = currentState.category
            )

            val result = ideaRepository.createIdea(newIdea)
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
