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

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _strategies = MutableStateFlow<List<Strategy>>(emptyList())

    val categories: StateFlow<List<String>> = _strategies.map { list ->
        list.mapNotNull { it.category?.trim() }.filter { it.isNotBlank() }.distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredStrategies: StateFlow<List<Strategy>> = combine(
        _strategies,
        _selectedCategory,
        _searchQuery
    ) { list, category, query ->
        var filtered = list.filter { it.isPublished }
        if (!category.isNullOrBlank()) {
            filtered = filtered.filter { it.category?.equals(category, ignoreCase = true) == true }
        }
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadStrategies()
    }

    fun loadStrategies() {
        viewModelScope.launch {
            _isLoading.value = true
            strategyRepository.getActiveStrategies().collect {
                _strategies.value = it
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }
}
