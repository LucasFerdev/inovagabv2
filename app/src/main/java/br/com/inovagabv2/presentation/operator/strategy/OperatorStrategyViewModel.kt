package br.com.inovagabv2.presentation.operator.strategy

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
class OperatorStrategyViewModel @Inject constructor(
    private val strategyRepository: StrategyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _strategies = MutableStateFlow<List<Strategy>>(emptyList())
    
    val filteredStrategies: StateFlow<List<Strategy>> = combine(_strategies, _searchQuery) { strategies, query ->
        if (query.isBlank()) {
            strategies
        } else {
            strategies.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true) 
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadStrategies()
    }

    private fun loadStrategies() {
        viewModelScope.launch {
            strategyRepository.getStrategies().collect {
                _strategies.value = it
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
