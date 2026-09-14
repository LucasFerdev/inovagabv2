package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.DashboardStrategyDetails
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.DashboardRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            launch {
                strategyRepository.getStrategyById(strategyId).collect { strat ->
                    _state.update { it.copy(strategy = strat) }
                }
            }

            launch {
                dashboardRepository.getDashboardStrategyDetails(strategyId).collect { details ->
                    _state.update { it.copy(dashboardDetails = details) }
                }
            }

            launch {
                ideaRepository.getIdeasRemote(estrategiaId = strategyId).collect { page ->
                    _state.update { it.copy(linkedIdeas = page.conteudo) }
                }
            }

            launch {
                projectRepository.getProjectsRemote(estrategiaId = strategyId).collect { page ->
                    _state.update { it.copy(linkedProjects = page.conteudo, isLoading = false) }
                }
            }
        }
    }
}
