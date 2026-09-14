package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.DashboardProjectDetails
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeadershipProjectDashboardState(
    val projectDetails: DashboardProjectDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class LeadershipProjectDashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _state = MutableStateFlow(LeadershipProjectDashboardState())
    val state: StateFlow<LeadershipProjectDashboardState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            dashboardRepository.getDashboardProjectDetails(projectId).collect { details ->
                if (details != null) {
                    _state.update { it.copy(projectDetails = details, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Indicadores do projeto não encontrados.") }
                }
            }
        }
    }
}
