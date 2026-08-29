package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadershipStrategyViewModel @Inject constructor(
    private val repository: StrategyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeadershipStrategyState())
    val state: StateFlow<LeadershipStrategyState> = _state.asStateFlow()

    init {
        loadStrategies()
    }

    fun loadStrategies() {
        repository.getStrategies()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { strategies ->
                _state.update { it.copy(isLoading = false, strategies = strategies) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun deleteStrategy(id: String) {
        viewModelScope.launch {
            repository.deleteStrategy(id)
            loadStrategies()
        }
    }

    fun togglePublish(strategy: Strategy) {
        viewModelScope.launch {
            repository.updateStrategy(strategy.copy(isPublished = !strategy.isPublished))
            loadStrategies()
        }
    }
}

data class LeadershipStrategyState(
    val isLoading: Boolean = false,
    val strategies: List<Strategy> = emptyList(),
    val error: String? = null
)
