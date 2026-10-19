package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.LOAD_FAILED_MESSAGE
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.DashboardStrategyDetails
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.DashboardRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

data class StrategyDashboardState(
    val strategy: Strategy? = null,
    val dashboardDetails: DashboardStrategyDetails? = null,
    val linkedIdeas: List<Idea> = emptyList(),
    val linkedProjects: List<Project> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class StrategyDashboardViewModel @Inject constructor(
    private val strategyRepository: StrategyRepository,
    private val ideaRepository: IdeaRepository,
    private val projectRepository: ProjectRepository,
    private val dashboardRepository: DashboardRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val strategyId: String = checkNotNull(savedStateHandle["strategyId"])

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _state = MutableStateFlow(StrategyDashboardState())
    val state: StateFlow<StrategyDashboardState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val showSpinner = _state.value.strategy == null
            _state.update {
                it.copy(
                    isLoading = showSpinner,
                    error = if (showSpinner) null else it.error
                )
            }
            var failure: String? = null
            supervisorScope {
                val jobs = listOf(
                    launch {
                        try {
                            strategyRepository.getStrategyById(strategyId)
                                .catch { e ->
                                    if (e is CancellationException) throw e
                                    failure = e.toUserMessage()
                                }
                                .collect { strat ->
                                    if (strat != null) {
                                        _state.update { it.copy(strategy = strat) }
                                    }
                                }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            failure = e.toUserMessage()
                        }
                    },
                    launch {
                        try {
                            dashboardRepository.getDashboardStrategyDetails(strategyId)
                                .catch { e ->
                                    if (e is CancellationException) throw e
                                    failure = e.toUserMessage()
                                }
                                .collect { details -> _state.update { it.copy(dashboardDetails = details) } }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            failure = e.toUserMessage()
                        }
                    },
                    launch {
                        try {
                            ideaRepository.getIdeasRemote(estrategiaId = strategyId)
                                .catch { e ->
                                    if (e is CancellationException) throw e
                                    failure = e.toUserMessage()
                                }
                                .collect { page -> _state.update { it.copy(linkedIdeas = page.conteudo) } }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            failure = e.toUserMessage()
                        }
                    },
                    launch {
                        try {
                            projectRepository.getProjectsRemote(estrategiaId = strategyId)
                                .catch { e ->
                                    if (e is CancellationException) throw e
                                    failure = e.toUserMessage()
                                }
                                .collect { page -> _state.update { it.copy(linkedProjects = page.conteudo) } }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            failure = e.toUserMessage()
                        }
                    }
                )
                jobs.forEach { it.join() }
            }
            _state.update { current ->
                current.copy(
                    isLoading = false,
                    error = when {
                        failure != null && current.strategy == null -> failure
                        current.strategy == null -> LOAD_FAILED_MESSAGE
                        else -> failure
                    }
                )
            }
        }
    }
}

fun canEditStrategy(role: Role?): Boolean = role == Role.LIDERANCA
