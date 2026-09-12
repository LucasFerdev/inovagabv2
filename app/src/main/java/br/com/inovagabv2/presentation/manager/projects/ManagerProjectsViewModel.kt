package br.com.inovagabv2.presentation.manager.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _allProjects = MutableStateFlow<List<Project>>(emptyList())
    private val _selectedTab = MutableStateFlow(0) // 0: Meus, 1: Todos

    val state = combine(_allProjects, _selectedTab) { projects, tab ->
        ManagerProjectsState(
            projects = projects,
            isLoading = false,
            selectedTab = tab
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ManagerProjectsState(isLoading = true))

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            projectRepository.getProjects().collect { projects ->
                _allProjects.value = projects
            }
        }
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }
}

data class ManagerProjectsState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    val error: String? = null
)
