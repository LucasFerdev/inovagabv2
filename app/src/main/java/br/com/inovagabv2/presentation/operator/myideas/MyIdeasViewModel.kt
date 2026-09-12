package br.com.inovagabv2.presentation.operator.myideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = sessionManager.userSession.first()

            // Fetch strategies to map strategyId -> strategyTitle
            launch {
                strategyRepository.getStrategies().collect { strategies ->
                    _strategiesMap.value = strategies.associate { it.id to it.title }
                }
            }

            // Fetch user's ideas
            if (currentUser != null) {
                ideaRepository.getIdeasByAuthor(currentUser.id).collect { userIdeas ->
                    _ideas.value = userIdeas
                    _isLoading.value = false
                }
            } else {
                ideaRepository.getIdeas().collect { allIdeas ->
                    _ideas.value = allIdeas
                    _isLoading.value = false
                }
            }
        }
    }
}
