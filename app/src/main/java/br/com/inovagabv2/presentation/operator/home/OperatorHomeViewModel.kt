package br.com.inovagabv2.presentation.operator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
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

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val hasData = _state.value.recentIdeas.isNotEmpty() || _state.value.strategies.isNotEmpty()
            _state.update { it.copy(isLoading = !hasData, error = null) }
            try {
                val currentUser = authRepository.getCurrentUser().first()
                val userId = currentUser?.id?.trim().orEmpty()
                if (userId.isEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Não foi possível identificar o usuário. Entre novamente para continuar."
                        )
                    }
                    return@launch
                }

                combine(
                    ideaRepository.getIdeasByAuthor(userId),
                    strategyRepository.getActiveStrategies()
                ) { ideas, strategies ->
                    val sortedIdeas = ideas.sortedByDescending { it.updatedAt ?: it.createdAt }
                    OperatorHomeState(
                        isLoading = false,
                        userName = currentUser?.name?.takeIf { it.isNotBlank() } ?: "Operador",
                        sentCount = ideas.count { it.status == IdeaStatus.ENVIADA },
                        inAnalysisCount = ideas.count { it.status == IdeaStatus.EM_ANALISE },
                        approvedCount = ideas.count { it.status == IdeaStatus.APROVADA },
                        recentIdeas = sortedIdeas.take(3),
                        strategies = strategies.take(2)
                    )
                }.collect { newState ->
                    _state.value = newState
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Não foi possível carregar os dados. Verifique sua conexão e tente novamente."
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.toUserMessage()
                    )
                }
            }
        }
    }
}
