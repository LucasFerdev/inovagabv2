package br.com.inovagabv2.presentation.manager.ideas.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerIdeaDetailsState(
    val idea: Idea? = null,
    val aiAnalysis: AiAnalysis? = null,
    val historyItems: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingAi: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmittingAi: Boolean = false,
    val selectedPriority: Int? = 3,
    val rejectionReason: String = "",
    val feedbackMessage: String? = null,
    val errorMessage: String? = null,
    val showApprovalDialog: Boolean = false,
    val showRejectionDialog: Boolean = false,
    val showRecalculateDialog: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val shouldNavigateBack: Boolean = false
)

@HiltViewModel
class ManagerIdeaDetailsViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val aiRepository: AiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ideaId: String = checkNotNull(savedStateHandle["ideaId"])

    private val _state = MutableStateFlow(ManagerIdeaDetailsState())
    val state: StateFlow<ManagerIdeaDetailsState> = _state.asStateFlow()

    init {
        loadIdea()
        loadAiAnalysis()
    }

    fun loadIdea() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            ideaRepository.getIdeaById(ideaId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Erro ao carregar ideia.") }
                }
                .collect { idea ->
                    _state.update {
                        it.copy(
                            idea = idea,
                            isLoading = false,
                            selectedPriority = idea?.priority ?: 3
                        )
                    }
                }
        }
    }

    fun loadAiAnalysis() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAi = true) }
            val result = aiRepository.getIdeaAnalysis(ideaId)
            if (result.isSuccess) {
                _state.update { it.copy(aiAnalysis = result.getOrNull(), isLoadingAi = false) }
            } else {
                _state.update { it.copy(isLoadingAi = false) }
            }
        }
    }

    fun onShowHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = true, isLoadingHistory = true) }
        viewModelScope.launch {
            try {
                ideaRepository.consultarHistorico(ideaId)
                    .catch { e ->
                        _state.update {
                            it.copy(
                                isLoadingHistory = false,
                                errorMessage = e.message ?: "Erro ao carregar histórico."
                            )
                        }
                    }
                    .collect { history ->
                        _state.update { it.copy(historyItems = history, isLoadingHistory = false) }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingHistory = false,
                        errorMessage = e.message ?: "Erro ao carregar histórico."
                    )
                }
            }
        }
    }

    fun onDismissHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = false) }
    }

    fun onAnalyzeAi() {
        if (_state.value.isSubmittingAi) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmittingAi = true, errorMessage = null) }
            val result = aiRepository.analyzeIdea(ideaId, recalculate = false)
            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        aiAnalysis = result.getOrNull(),
                        isSubmittingAi = false,
                        feedbackMessage = "Análise realizada com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSubmittingAi = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Erro ao gerar análise da IA"
                    )
                }
            }
        }
    }

    fun onShowRecalculateDialog() {
        if (_state.value.isSubmittingAi) return
        _state.update { it.copy(showRecalculateDialog = true) }
    }

    fun onDismissRecalculateDialog() {
        _state.update { it.copy(showRecalculateDialog = false) }
    }

    fun onConfirmRecalculateAi() {
        if (_state.value.isSubmittingAi) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmittingAi = true, showRecalculateDialog = false, errorMessage = null) }
            val result = aiRepository.analyzeIdea(ideaId, recalculate = true)
            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        aiAnalysis = result.getOrNull(),
                        isSubmittingAi = false,
                        feedbackMessage = "Análise recalculada com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSubmittingAi = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Erro ao recalcular análise da IA"
                    )
                }
            }
        }
    }

    fun onPrioritySelected(priority: Int) {
        if (_state.value.isSubmitting) return
        _state.update { it.copy(selectedPriority = priority) }
        
        viewModelScope.launch {
            val res = ideaRepository.priorizar(ideaId, priority)
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        idea = res.getOrNull(),
                        feedbackMessage = "Prioridade atualizada com sucesso."
                    )
                }
            }
        }
    }

    fun onRejectionReasonChange(reason: String) {
        _state.update { it.copy(rejectionReason = reason) }
    }

    fun onShowApprovalDialog() {
        if (_state.value.isSubmitting) return
        if (_state.value.selectedPriority == null) {
            _state.update { it.copy(errorMessage = "Selecione uma prioridade (P1 a P5) antes de aprovar.") }
            return
        }
        _state.update { it.copy(showApprovalDialog = true) }
    }

    fun onDismissApprovalDialog() {
        _state.update { it.copy(showApprovalDialog = false) }
    }

    fun onConfirmApproval() {
        val priority = _state.value.selectedPriority ?: 3
        if (_state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, showApprovalDialog = false, errorMessage = null) }

            val priorizarRes = ideaRepository.priorizar(ideaId, priority)
            val aprovarRes = ideaRepository.aprovar(ideaId)

            if (aprovarRes.isSuccess) {
                val updatedIdea = aprovarRes.getOrNull() ?: priorizarRes.getOrNull()
                _state.update {
                    it.copy(
                        idea = updatedIdea,
                        isSubmitting = false,
                        feedbackMessage = "Ideia aprovada com sucesso.",
                        shouldNavigateBack = true
                    )
                }
            } else {
                val errorMsg = aprovarRes.exceptionOrNull()?.message ?: "Erro ao aprovar ideia"
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }

    fun onShowRejectionDialog() {
        if (_state.value.isSubmitting) return
        _state.update { it.copy(showRejectionDialog = true) }
    }

    fun onDismissRejectionDialog() {
        _state.update { it.copy(showRejectionDialog = false) }
    }

    fun onConfirmRejection() {
        val reason = _state.value.rejectionReason.trim()
        if (reason.length < 3) {
            _state.update { it.copy(errorMessage = "A justificativa deve ter pelo menos 3 caracteres.") }
            return
        }
        if (_state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, showRejectionDialog = false, errorMessage = null) }

            val result = ideaRepository.rejeitar(ideaId, reason)
            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        idea = result.getOrNull(),
                        isSubmitting = false,
                        feedbackMessage = "Ideia rejeitada com sucesso.",
                        shouldNavigateBack = true
                    )
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Erro ao rejeitar ideia"
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }

    fun onAnalisar() {
        if (_state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }

            val result = ideaRepository.analisar(ideaId)
            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        idea = result.getOrNull(),
                        isSubmitting = false,
                        feedbackMessage = "Ideia enviada para análise."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Erro ao colocar ideia em análise"
                    )
                }
            }
        }
    }

    fun clearFeedbackMessage() {
        _state.update { it.copy(feedbackMessage = null) }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }
}
