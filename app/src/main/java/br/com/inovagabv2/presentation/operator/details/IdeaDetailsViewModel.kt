package br.com.inovagabv2.presentation.operator.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeaDetailsViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val aiRepository: AiRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ideaId: String = checkNotNull(savedStateHandle["ideaId"])

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _idea = MutableStateFlow<Idea?>(null)
    val idea: StateFlow<Idea?> = _idea.asStateFlow()

    private val _aiAnalysis = MutableStateFlow<AiAnalysis?>(null)
    val aiAnalysis: StateFlow<AiAnalysis?> = _aiAnalysis.asStateFlow()

    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    init {
        loadIdea()
        loadAiAnalysisForLeadership()
    }

    private fun loadIdea() {
        viewModelScope.launch {
            ideaRepository.getIdeaById(ideaId).collect {
                _idea.value = it
            }
        }
    }

    private fun loadAiAnalysisForLeadership() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser().first()
            if (currentUser?.role == Role.LIDERANCA || currentUser?.role == Role.GESTOR) {
                _isLoadingAi.value = true
                val result = aiRepository.getIdeaAnalysis(ideaId)
                if (result.isSuccess) {
                    _aiAnalysis.value = result.getOrNull()
                    _aiError.value = null
                } else {
                    _aiAnalysis.value = null
                    _aiError.value = "Esta ideia ainda não possui análise da IA."
                }
                _isLoadingAi.value = false
            }
        }
    }
}
