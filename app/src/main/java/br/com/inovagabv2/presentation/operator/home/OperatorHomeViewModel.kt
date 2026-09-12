package br.com.inovagabv2.presentation.operator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OperatorHomeViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val strategyRepository: StrategyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _state = MutableStateFlow(OperatorHomeState())
    val state: StateFlow<OperatorHomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            combine(
                user,
                ideaRepository.getIdeas(),
                strategyRepository.getStrategies()
            ) { currentUser, ideas, strategies ->
                val userIdeas = if (currentUser != null) {
                    ideas.filter { it.authorId == currentUser.id }
                } else {
                    ideas
                }
                val sortedIdeas = userIdeas.sortedByDescending { it.updatedAt ?: it.createdAt }
                OperatorHomeState(
                    isLoading = false,
                    userName = currentUser?.name ?: "Operador",
                    sentCount = userIdeas.count { it.status == IdeaStatus.ENVIADA },
                    inAnalysisCount = userIdeas.count { it.status == IdeaStatus.EM_ANALISE },
                    approvedCount = userIdeas.count { it.status == IdeaStatus.APROVADA },
                    recentIdeas = sortedIdeas.take(3),
                    strategies = strategies.take(2)
                )
            }.catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
