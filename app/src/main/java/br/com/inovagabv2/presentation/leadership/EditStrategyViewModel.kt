package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.StrategyStatus
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditStrategyState(
    val strategy: Strategy? = null,
    val historyItems: List<HistoryItem> = emptyList(),
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val category: String = "",
    val campaign: String = "",
    val status: StrategyStatus = StrategyStatus.RASCUNHO,
    val isLoading: Boolean = true,
    val isLoadingHistory: Boolean = false,
    val isSaving: Boolean = false,
    val showArchiveDialog: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null,
    val shouldNavigateBack: Boolean = false
) {
    val isValid: Boolean get() = title.isNotBlank() && description.isNotBlank() && category.isNotBlank() && campaign.isNotBlank()
}

@HiltViewModel
class EditStrategyViewModel @Inject constructor(
    private val repository: StrategyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val strategyId: String = checkNotNull(savedStateHandle["strategyId"])

    private val _state = MutableStateFlow(EditStrategyState())
    val state = _state.asStateFlow()

    init {
        loadStrategy()
    }

    fun loadStrategy() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getStrategyById(strategyId).collect { strategy ->
                if (strategy != null) {
                    _state.update {
                        it.copy(
                            strategy = strategy,
                            title = strategy.title,
                            description = strategy.description,
                            date = strategy.date,
                            category = strategy.category ?: "",
                            campaign = strategy.campaign ?: "",
                            status = strategy.status,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Estratégia não encontrada.") }
                }
            }
        }
    }

    fun onShowHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = true, isLoadingHistory = true) }
        viewModelScope.launch {
            repository.consultarHistorico(strategyId).collect { history ->
                _state.update { it.copy(historyItems = history, isLoadingHistory = false) }
            }
        }
    }

    fun onDismissHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = false) }
    }

    fun onTitleChange(v: String) = _state.update { it.copy(title = v) }
    fun onDescriptionChange(v: String) = _state.update { it.copy(description = v) }
    fun onDateChange(v: String) = _state.update { it.copy(date = v) }
    fun onCategoryChange(v: String) = _state.update { it.copy(category = v) }
    fun onCampaignChange(v: String) = _state.update { it.copy(campaign = v) }

    fun onUpdateStrategy() {
        val cur = _state.value
        if (!cur.isValid || cur.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val res = repository.updateStrategy(
                id = strategyId,
                titulo = cur.title,
                descricao = cur.description,
                data = cur.date,
                categoria = cur.category,
                campanha = cur.campaign
            )
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        strategy = res.getOrNull(),
                        status = res.getOrNull()?.status ?: cur.status,
                        isSaving = false,
                        feedbackMessage = "Estratégia atualizada com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao atualizar estratégia"
                    )
                }
            }
        }
    }

    fun onActivateStrategy() {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val res = repository.activateStrategy(strategyId)
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        strategy = res.getOrNull(),
                        status = StrategyStatus.ATIVA,
                        isSaving = false,
                        feedbackMessage = "Estratégia ativada com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao ativar estratégia"
                    )
                }
            }
        }
    }

    fun onDeactivateStrategy() {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val res = repository.deactivateStrategy(strategyId)
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        strategy = res.getOrNull(),
                        status = StrategyStatus.INATIVA,
                        isSaving = false,
                        feedbackMessage = "Estratégia desativada com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao desativar estratégia"
                    )
                }
            }
        }
    }

    fun onShowArchiveDialog() = _state.update { it.copy(showArchiveDialog = true) }
    fun onDismissArchiveDialog() = _state.update { it.copy(showArchiveDialog = false) }

    fun onArchiveStrategy() {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, showArchiveDialog = false, errorMessage = null) }
            val res = repository.archiveStrategy(strategyId)
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        feedbackMessage = "Estratégia arquivada com sucesso.",
                        shouldNavigateBack = true
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao arquivar estratégia"
                    )
                }
            }
        }
    }

    fun clearFeedbackMessage() = _state.update { it.copy(feedbackMessage = null) }
    fun clearErrorMessage() = _state.update { it.copy(errorMessage = null) }
}
