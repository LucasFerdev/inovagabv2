package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LeadershipProjectsViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeadershipProjectsState())
    val state = _state.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        repository.getProjects()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { projects ->
                _state.update { it.copy(
                    isLoading = false, 
                    projects = projects,
                    totalInvestment = projects.sumOf { 
                        it.investment.replace("R$", "").replace(".", "").replace(",", ".").trim().toDoubleOrNull() ?: 0.0 
                    }
                ) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }
}

data class LeadershipProjectsState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val totalInvestment: Double = 0.0,
    val error: String? = null
)
