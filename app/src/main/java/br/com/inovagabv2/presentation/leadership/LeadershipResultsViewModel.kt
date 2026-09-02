package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LeadershipResultsViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeadershipResultsState())
    val state = _state.asStateFlow()

    init {
        loadResults()
    }

    private fun loadResults() {
        repository.getDashboardData()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { data ->
                _state.update { it.copy(isLoading = false, data = data) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }
}

data class LeadershipResultsState(
    val isLoading: Boolean = false,
    val data: DashboardData? = null,
    val error: String? = null
)
