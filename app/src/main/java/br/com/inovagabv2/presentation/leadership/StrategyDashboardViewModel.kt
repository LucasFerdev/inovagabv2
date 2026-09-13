package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StrategyDashboardViewModel @Inject constructor(
    private val strategyRepository: StrategyRepository,
    private val ideaRepository: IdeaRepository,
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val strategyId: String = checkNotNull(savedStateHandle["strategyId"])

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _strategy = MutableStateFlow<Strategy?>(null)
    val strategy: StateFlow<Strategy?> = _strategy.asStateFlow()

    private val _linkedIdeas = MutableStateFlow<List<Idea>>(emptyList())
    val linkedIdeas: StateFlow<List<Idea>> = _linkedIdeas.asStateFlow()

    private val _linkedProjects = MutableStateFlow<List<Project>>(emptyList())
    val linkedProjects: StateFlow<List<Project>> = _linkedProjects.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true

            launch {
                strategyRepository.getStrategyById(strategyId).collect {
                    _strategy.value = it
                }
            }

            launch {
                ideaRepository.getIdeas().collect { allIdeas ->
                    _linkedIdeas.value = allIdeas.filter { it.strategyId == strategyId }
                    _isLoading.value = false
                }
            }

            launch {
                projectRepository.getProjects().collect { allProjects ->
                    _linkedProjects.value = allProjects
                }
            }
        }
    }
}
