package br.com.inovagabv2.presentation.manager.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerHomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val ideaRepository: IdeaRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ManagerHomeState())
    val state: StateFlow<ManagerHomeState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val current = _state.value
            val showLoading = current.user == null && current.ideas.isEmpty()
            _state.update { it.copy(isLoading = if (showLoading) true else it.isLoading, error = null) }
            try {
                combine(
                    authRepository.getCurrentUser(),
                    ideaRepository.getIdeas(),
                    projectRepository.getProjects()
                ) { user, ideas, projects ->
                    val metrics = ManagerMetrics(
                        receivedIdeas = ideas.size,
                        inAnalysisIdeas = ideas.count { it.status == IdeaStatus.EM_ANALISE },
                        approvedIdeas = ideas.count { it.status == IdeaStatus.APROVADA },
                        activeProjects = projects.size
                    )

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            user = user,
                            ideas = ideas.filter { idea -> idea.status == IdeaStatus.ENVIADA || idea.status == IdeaStatus.EM_ANALISE }.take(3),
                            metrics = metrics
                        )
                    }
                }.catch { e ->
                    if (e is CancellationException) throw e
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Não foi possível carregar os dados. Tente novamente."
                        )
                    }
                }.collect()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Não foi possível carregar os dados. Tente novamente."
                    )
                }
            }
        }
    }
}
