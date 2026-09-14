package br.com.inovagabv2.presentation.manager.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerProjectsState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true,
    val selectedTab: Int = 0,
    val error: String? = null
)

@HiltViewModel
class ManagerProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    private val _state = MutableStateFlow(ManagerProjectsState())
    val state: StateFlow<ManagerProjectsState> = _state.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val currentUser = authRepository.getCurrentUser().first()
            val gestorId = if (_selectedTab.value == 0) currentUser?.id else null

            projectRepository.getProjectsRemote(gestorId = gestorId)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erro ao carregar projetos."
                        )
                    }
                }
                .collect { page ->
                    _state.update {
                        it.copy(
                            projects = page.conteudo,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onTabSelected(index: Int) {
        if (_selectedTab.value == index) return
        _selectedTab.value = index
        _state.update { it.copy(selectedTab = index) }
        loadProjects()
    }
}
