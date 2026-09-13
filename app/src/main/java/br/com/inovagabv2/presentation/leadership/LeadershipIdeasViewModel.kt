package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadershipIdeasViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _allIdeas = MutableStateFlow<List<Idea>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<IdeaStatus?>(null)
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val filteredIdeas: StateFlow<List<Idea>> = combine(_allIdeas, _searchQuery, _selectedStatus) { ideas, query, status ->
        ideas.filter { idea ->
            (query.isBlank() || idea.title.contains(query, ignoreCase = true) || idea.description.contains(query, ignoreCase = true)) &&
            (status == null || idea.status == status)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inAnalysisCount: StateFlow<Int> = _allIdeas.map { list -> list.count { it.status == IdeaStatus.EM_ANALISE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val approvedCount: StateFlow<Int> = _allIdeas.map { list -> list.count { it.status == IdeaStatus.APROVADA } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val rejectedCount: StateFlow<Int> = _allIdeas.map { list -> list.count { it.status == IdeaStatus.REJEITADA } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadIdeas()
    }

    fun loadIdeas() {
        viewModelScope.launch {
            _isLoading.value = true
            ideaRepository.getIdeas().collect { ideas ->
                _allIdeas.value = ideas
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: IdeaStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }
}
