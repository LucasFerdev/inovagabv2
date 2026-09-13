package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadershipStrategyViewModel @Inject constructor(
    private val repository: StrategyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _strategies = MutableStateFlow<List<Strategy>>(emptyList())
    private val _selectedStatusFilter = MutableStateFlow<String?>(null) // null = Todas, "ATIVA", "RASCUNHO", "INATIVA"
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val filteredStrategies: StateFlow<List<Strategy>> = combine(_strategies, _selectedStatusFilter, _searchQuery) { list, statusFilter, query ->
        list.filter { strat ->
            val matchesStatus = when (statusFilter) {
                "ATIVA" -> strat.isPublished
                "RASCUNHO" -> !strat.isPublished
                else -> true
            }
            val matchesQuery = query.isBlank() || strat.title.contains(query, ignoreCase = true) || strat.description.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCount: StateFlow<Int> = _strategies.map { list -> list.count { it.isPublished } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val draftCount: StateFlow<Int> = _strategies.map { list -> list.count { !it.isPublished } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadStrategies()
    }

    fun loadStrategies() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getStrategies().collect { list ->
                _strategies.value = list
                _isLoading.value = false
            }
        }
    }

    fun onStatusFilterSelected(status: String?) {
        _selectedStatusFilter.value = if (_selectedStatusFilter.value == status) null else status
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteStrategy(id: String) {
        viewModelScope.launch {
            repository.deleteStrategy(id)
        }
    }
}
