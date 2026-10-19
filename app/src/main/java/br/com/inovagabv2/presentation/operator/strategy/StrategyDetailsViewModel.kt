package br.com.inovagabv2.presentation.operator.strategy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.LOAD_FAILED_MESSAGE
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StrategyDetailsState(
    val strategy: Strategy? = null,
    val historyItems: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingHistory: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StrategyDetailsViewModel @Inject constructor(
    private val strategyRepository: StrategyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val strategyId: String = checkNotNull(savedStateHandle["strategyId"])

    private val _state = MutableStateFlow(StrategyDetailsState())
    val state: StateFlow<StrategyDetailsState> = _state.asStateFlow()

    init {
        loadStrategyDetails()
    }

    fun loadStrategyDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                strategyRepository.getStrategyById(strategyId)
                    .catch { e ->
                        if (e is CancellationException) throw e
                        _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }
                    }
                    .collect { strategy ->
                        if (strategy != null) {
                            _state.update { it.copy(strategy = strategy, isLoading = false, error = null) }
                        } else {
                            _state.update { it.copy(isLoading = false, error = LOAD_FAILED_MESSAGE) }
                        }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
        }
    }

    fun onShowHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = true, isLoadingHistory = true, error = null) }
        viewModelScope.launch {
            try {
                strategyRepository.consultarHistorico(strategyId)
                    .catch { e ->
                        if (e is CancellationException) throw e
                        _state.update { it.copy(isLoadingHistory = false, error = e.toUserMessage()) }
                    }
                    .collect { history ->
                        _state.update { it.copy(historyItems = history, isLoadingHistory = false) }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingHistory = false, error = e.toUserMessage()) }
            }
        }
    }

    fun onDismissHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = false) }
    }
}
