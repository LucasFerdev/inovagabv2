package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.DashboardProjectDetails
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.repository.DashboardRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeadershipProjectDetailsState(
    val project: Project? = null,
    val dashboardDetails: DashboardProjectDetails? = null,
    val historyItems: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingHistory: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LeadershipProjectDetailsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val dashboardRepository: DashboardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _state = MutableStateFlow(LeadershipProjectDetailsState())
    val state: StateFlow<LeadershipProjectDetailsState> = _state.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            launch {
                projectRepository.getProjectById(projectId).collect { proj ->
                    _state.update { it.copy(project = proj) }
                }
            }

            launch {
                dashboardRepository.getDashboardProjectDetails(projectId).collect { details ->
                    _state.update { it.copy(dashboardDetails = details, isLoading = false) }
                }
            }
        }
    }

    fun onShowHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = true, isLoadingHistory = true) }
        viewModelScope.launch {
            projectRepository.consultarHistorico(projectId).collect { history ->
                _state.update { it.copy(historyItems = history, isLoadingHistory = false) }
            }
        }
    }

    fun onDismissHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = false) }
    }
}
