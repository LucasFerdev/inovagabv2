package br.com.inovagabv2.presentation.manager.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val showLoading = _state.value.projects.isEmpty()
            _state.update {
                it.copy(
                    isLoading = if (showLoading) true else it.isLoading,
                    error = null,
                    selectedTab = _selectedTab.value
                )
            }
            try {
                val currentUser = authRepository.getCurrentUser().first()
                val gestorId = if (_selectedTab.value == 0) currentUser?.id else null

                projectRepository.getProjectsRemote(gestorId = gestorId)
                    .catch { e ->
                        if (e is CancellationException) throw e
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
                                error = null,
                                selectedTab = _selectedTab.value
                            )
                        }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar projetos."
                    )
                }
            }
        }
    }

    fun loadProjects() {
        refresh()
    }

    fun onTabSelected(index: Int) {
        if (_selectedTab.value == index) return
        _selectedTab.value = index
        _state.update {
            it.copy(selectedTab = index, projects = emptyList(), isLoading = true, error = null)
        }
        refresh()
    }
}
