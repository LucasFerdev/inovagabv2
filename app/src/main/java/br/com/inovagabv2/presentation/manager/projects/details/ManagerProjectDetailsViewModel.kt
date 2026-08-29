package br.com.inovagabv2.presentation.manager.projects.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerProjectDetailsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _state = MutableStateFlow(ManagerProjectDetailsState())
    val state: StateFlow<ManagerProjectDetailsState> = _state.asStateFlow()

    init {
        loadProject()
    }

    private fun loadProject() {
        viewModelScope.launch {
            projectRepository.getProjectById(projectId).collect { project ->
                _state.update { it.copy(project = project, isLoading = false) }
            }
        }
    }

    fun updateProgress(newProgress: Float) {
        val currentProject = _state.value.project ?: return
        viewModelScope.launch {
            val updatedProject = currentProject.copy(progress = newProgress)
            projectRepository.updateProject(updatedProject)
        }
    }
}

data class ManagerProjectDetailsState(
    val project: Project? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
