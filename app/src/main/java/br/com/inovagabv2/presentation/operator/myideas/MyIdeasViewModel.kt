package br.com.inovagabv2.presentation.operator.myideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyIdeasViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val strategyRepository: StrategyRepository,
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _ideas = MutableStateFlow<List<Idea>>(emptyList())
    val ideas: StateFlow<List<Idea>> = _ideas.asStateFlow()

    private val _strategiesMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val strategiesMap: StateFlow<Map<String, String>> = _strategiesMap.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (_ideas.value.isEmpty()) {
                _isLoading.value = true
            }
            _error.value = null
            try {
                val currentUser = sessionManager.userSession.first()

                launch {
                    strategyRepository.getStrategies().collect { strategies ->
                        _strategiesMap.value = strategies.associate { it.id to it.title }
                    }
                }

                val ideasFlow = if (currentUser != null) {
                    ideaRepository.getIdeasByAuthor(currentUser.id)
                } else {
                    ideaRepository.getIdeas()
                }
                ideasFlow.collect { userIdeas ->
                    _ideas.value = userIdeas
                    _isLoading.value = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.toUserMessage()
            }
        }
    }

    fun loadData() {
        refresh()
    }
}
