package br.com.inovagabv2.presentation.manager.ideas.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.LOAD_FAILED_MESSAGE
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerIdeaDetailsState(
    val idea: Idea? = null,
    val aiAnalysis: AiAnalysis? = null,
    val historyItems: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: String? = null,
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
            _state.update { it.copy(isLoading = true, loadError = null) }
            try {
                ideaRepository.getIdeaById(ideaId)
                    .catch { e ->
                        if (e is CancellationException) throw e
                        _state.update { it.copy(isLoading = false, loadError = e.toUserMessage()) }
                    }
                    .collect { idea ->
                        if (idea != null) {
                            _state.update {
                                it.copy(
                                    idea = idea,
                                    isLoading = false,
                                    loadError = null,
                                    selectedPriority = idea.priority ?: 3
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(idea = null, isLoading = false, loadError = LOAD_FAILED_MESSAGE)
                            }
                        }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, loadError = e.toUserMessage()) }
            }
        }
    }

    fun loadAiAnalysis() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAi = true) }
            try {
                val result = aiRepository.getIdeaAnalysis(ideaId)
                if (result.isSuccess) {
                    _state.update { it.copy(aiAnalysis = result.getOrNull(), isLoadingAi = false) }
                } else {
                    _state.update { it.copy(isLoadingAi = false) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingAi = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    fun onShowHistoryDialog() {
        _state.update { it.copy(showHistoryDialog = true, isLoadingHistory = true) }
        viewModelScope.launch {
            try {
                ideaRepository.consultarHistorico(ideaId)
                    .catch { e ->
                        if (e is CancellationException) throw e
                        _state.update {
                            it.copy(
                                isLoadingHistory = false,
                                errorMessage = e.toUserMessage()
                            )
                        }
                    }
                    .collect { history ->
                        _state.update { it.copy(historyItems = history, isLoadingHistory = false) }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingHistory = false,
                        errorMessage = e.toUserMessage()
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
        val status = _state.value.idea?.status ?: return
        if (!showAnalyzeWithAi(status, _state.value.aiAnalysis != null)) return

        _state.update { it.copy(isSubmittingAi = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                if (_state.value.idea?.status == IdeaStatus.ENVIADA) {
                    val transition = ideaRepository.analisar(ideaId)
                    if (transition.isFailure) {
                        _state.update {
                            it.copy(
                                isSubmittingAi = false,
                                errorMessage = transition.exceptionOrNull()?.toUserMessage()
                            )
                        }
                        return@launch
                    }
                    val transitioned = transition.getOrNull()
                    val ideaInAnalysis = when {
                        transitioned == null -> _state.value.idea?.copy(status = IdeaStatus.EM_ANALISE)
                        transitioned.status == IdeaStatus.EM_ANALISE -> transitioned
                        else -> transitioned.copy(status = IdeaStatus.EM_ANALISE)
                    }
                    _state.update { it.copy(idea = ideaInAnalysis ?: it.idea) }
                }

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
                            errorMessage = result.exceptionOrNull()?.toUserMessage()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSubmittingAi = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    fun onShowRecalculateDialog() {
        if (_state.value.isSubmittingAi) return
        val status = _state.value.idea?.status ?: return
        if (!showRecalculateAi(status, _state.value.aiAnalysis != null)) return
        _state.update { it.copy(showRecalculateDialog = true) }
    }

    fun onDismissRecalculateDialog() {
        _state.update { it.copy(showRecalculateDialog = false) }
    }

    fun onConfirmRecalculateAi() {
        if (_state.value.isSubmittingAi) return
        val status = _state.value.idea?.status ?: return
        if (!showRecalculateAi(status, _state.value.aiAnalysis != null)) return

        _state.update { it.copy(isSubmittingAi = true, showRecalculateDialog = false, errorMessage = null) }
        viewModelScope.launch {
            try {
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
                            errorMessage = result.exceptionOrNull()?.toUserMessage()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSubmittingAi = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    fun onPrioritySelected(priority: Int) {
        if (_state.value.isSubmitting) return
        _state.update { it.copy(selectedPriority = priority) }
        // A API só aceita priorizar ideias EM_ANALISE; para ENVIADA a prioridade é aplicada na aprovação.
        if (_state.value.idea?.status != IdeaStatus.EM_ANALISE) return

        viewModelScope.launch {
            try {
                val res = ideaRepository.priorizar(ideaId, priority)
                if (res.isSuccess) {
                    _state.update {
                        it.copy(
                            idea = res.getOrNull(),
                            feedbackMessage = "Prioridade atualizada com sucesso."
                        )
                    }
                } else {
                    _state.update { it.copy(errorMessage = res.exceptionOrNull()?.toUserMessage()) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.toUserMessage()) }
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
            try {
                val transition = ensureInAnalysis()
                if (transition.isFailure) {
                    _state.update {
                        it.copy(isSubmitting = false, errorMessage = transition.exceptionOrNull()?.toUserMessage())
                    }
                    return@launch
                }
                val priorizarRes = ideaRepository.priorizar(ideaId, priority)
                if (priorizarRes.isFailure) {
                    _state.update {
                        it.copy(isSubmitting = false, errorMessage = priorizarRes.exceptionOrNull()?.toUserMessage())
                    }
                    return@launch
                }
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
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = aprovarRes.exceptionOrNull()?.toUserMessage()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    /** A API exige EM_ANALISE para priorizar, aprovar e rejeitar: move ENVIADA para EM_ANALISE antes. */
    private suspend fun ensureInAnalysis(): Result<Unit> {
        if (_state.value.idea?.status != IdeaStatus.ENVIADA) return Result.success(Unit)
        val transition = ideaRepository.analisar(ideaId)
        if (transition.isFailure) return Result.failure(transition.exceptionOrNull() ?: Exception())
        _state.update { st ->
            st.copy(idea = transition.getOrNull()?.copy(status = IdeaStatus.EM_ANALISE)
                ?: st.idea?.copy(status = IdeaStatus.EM_ANALISE))
        }
        return Result.success(Unit)
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
            try {
                val transition = ensureInAnalysis()
                if (transition.isFailure) {
                    _state.update {
                        it.copy(isSubmitting = false, errorMessage = transition.exceptionOrNull()?.toUserMessage())
                    }
                    return@launch
                }
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
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.exceptionOrNull()?.toUserMessage()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    fun onAnalisar() {
        if (_state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }
            try {
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
                            errorMessage = result.exceptionOrNull()?.toUserMessage()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, errorMessage = e.toUserMessage()) }
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

internal fun showAnalyzeWithAi(status: IdeaStatus, hasAnalysis: Boolean): Boolean {
    return !hasAnalysis && (status == IdeaStatus.ENVIADA || status == IdeaStatus.EM_ANALISE)
}

internal fun showRecalculateAi(status: IdeaStatus, hasAnalysis: Boolean): Boolean {
    return hasAnalysis && status == IdeaStatus.EM_ANALISE
}
