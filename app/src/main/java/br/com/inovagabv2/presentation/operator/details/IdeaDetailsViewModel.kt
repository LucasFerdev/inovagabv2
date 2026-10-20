package br.com.inovagabv2.presentation.operator.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.LOAD_FAILED_MESSAGE
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AiRepository
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeaDetailsViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val aiRepository: AiRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ideaId: String = checkNotNull(savedStateHandle["ideaId"])

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _idea = MutableStateFlow<Idea?>(null)
    val idea: StateFlow<Idea?> = _idea.asStateFlow()

    private val _isLoadingIdea = MutableStateFlow(true)
    val isLoadingIdea: StateFlow<Boolean> = _isLoadingIdea.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    private val _historyError = MutableStateFlow<String?>(null)
    val historyError: StateFlow<String?> = _historyError.asStateFlow()

    private val _aiAnalysis = MutableStateFlow<AiAnalysis?>(null)
    val aiAnalysis: StateFlow<AiAnalysis?> = _aiAnalysis.asStateFlow()

    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    private val _historyItems = MutableStateFlow<List<HistoryItem>>(emptyList())
    val historyItems: StateFlow<List<HistoryItem>> = _historyItems.asStateFlow()

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    private val _showHistoryDialog = MutableStateFlow(false)
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    // Edição e arquivamento: só o operador autor, enquanto a ideia está ENVIADA (regra da API)
    val canManage: StateFlow<Boolean> = combine(_idea, user) { idea, current ->
        idea != null && current != null &&
            current.role == Role.OPERADOR &&
            idea.status == IdeaStatus.ENVIADA &&
            (idea.authorId.isBlank() || idea.authorId == current.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _editTitle = MutableStateFlow("")
    val editTitle: StateFlow<String> = _editTitle.asStateFlow()
    private val _editProblem = MutableStateFlow("")
    val editProblem: StateFlow<String> = _editProblem.asStateFlow()
    private val _editSolution = MutableStateFlow("")
    val editSolution: StateFlow<String> = _editSolution.asStateFlow()
    private val _editBenefits = MutableStateFlow("")
    val editBenefits: StateFlow<String> = _editBenefits.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _showArchiveDialog = MutableStateFlow(false)
    val showArchiveDialog: StateFlow<Boolean> = _showArchiveDialog.asStateFlow()

    private val _archived = MutableStateFlow(false)
    val archived: StateFlow<Boolean> = _archived.asStateFlow()

    init {
        loadIdea()
        loadAiAnalysisForLeadership()
    }

    fun loadIdea() {
        viewModelScope.launch {
            _isLoadingIdea.value = true
            _loadError.value = null
            try {
                ideaRepository.getIdeaById(ideaId)
                    .catch { e ->
                        if (e is CancellationException) throw e
                        _isLoadingIdea.value = false
                        _loadError.value = e.toUserMessage()
                    }
                    .collect { loaded ->
                        _idea.value = loaded
                        _isLoadingIdea.value = false
                        _loadError.value = if (loaded == null) LOAD_FAILED_MESSAGE else null
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _isLoadingIdea.value = false
                _loadError.value = e.toUserMessage()
            }
        }
    }

    private fun loadAiAnalysisForLeadership() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser().first()
                if (currentUser?.role == Role.LIDERANCA || currentUser?.role == Role.GESTOR) {
                    _isLoadingAi.value = true
                    val result = aiRepository.getIdeaAnalysis(ideaId)
                    if (result.isSuccess) {
                        _aiAnalysis.value = result.getOrNull()
                        _aiError.value = null
                    } else {
                        _aiAnalysis.value = null
                        _aiError.value = "Esta ideia ainda não possui análise da IA."
                    }
                    _isLoadingAi.value = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _isLoadingAi.value = false
                _aiError.value = e.toUserMessage()
            }
        }
    }

    fun onShowHistoryDialog() {
        _showHistoryDialog.value = true
        _isLoadingHistory.value = true
        _historyError.value = null
        viewModelScope.launch {
            try {
                ideaRepository.consultarHistorico(ideaId)
                    .catch { e ->
                        if (e is CancellationException) throw e
                        _isLoadingHistory.value = false
                        _historyError.value = e.toUserMessage()
                    }
                    .collect { history ->
                        _historyItems.value = history
                        _isLoadingHistory.value = false
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _isLoadingHistory.value = false
                _historyError.value = e.toUserMessage()
            }
        }
    }

    fun onStartEdit() {
        val current = _idea.value ?: return
        _editTitle.value = current.title
        _editProblem.value = current.problem
        _editSolution.value = current.proposedSolution
        _editBenefits.value = current.expectedBenefits
        _isEditing.value = true
    }

    fun onCancelEdit() {
        _isEditing.value = false
    }

    fun onEditTitleChange(v: String) { _editTitle.value = v.replace("\n", " ") }
    fun onEditProblemChange(v: String) { _editProblem.value = v }
    fun onEditSolutionChange(v: String) { _editSolution.value = v }
    fun onEditBenefitsChange(v: String) { _editBenefits.value = v }

    fun onSaveEdit() {
        val current = _idea.value ?: return
        if (_isSubmitting.value) return
        val title = _editTitle.value.trim()
        val problem = _editProblem.value.trim()
        val solution = _editSolution.value.trim()
        val benefits = _editBenefits.value.trim()
        val error = when {
            title.length < 3 -> "O título deve ter pelo menos 3 caracteres."
            problem.length < 10 -> "Descreva o problema com pelo menos 10 caracteres."
            solution.length < 10 -> "Descreva a solução com pelo menos 10 caracteres."
            benefits.length < 10 -> "Descreva os benefícios com pelo menos 10 caracteres."
            else -> null
        }
        if (error != null) {
            _actionMessage.value = error
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val result = ideaRepository.updateIdea(
                    id = ideaId,
                    titulo = title,
                    problema = problem,
                    solucaoProposta = solution,
                    beneficiosEsperados = benefits,
                    categoria = current.category,
                    estrategiaId = current.strategyId
                )
                if (result.isSuccess) {
                    _idea.value = result.getOrNull() ?: current.copy(
                        title = title, problem = problem,
                        proposedSolution = solution, expectedBenefits = benefits
                    )
                    _isEditing.value = false
                    _actionMessage.value = "Ideia atualizada com sucesso."
                } else {
                    _actionMessage.value = result.exceptionOrNull()?.toUserMessage()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionMessage.value = e.toUserMessage()
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun onShowArchiveDialog() {
        if (_isSubmitting.value) return
        _showArchiveDialog.value = true
    }

    fun onDismissArchiveDialog() {
        _showArchiveDialog.value = false
    }

    fun onConfirmArchive() {
        if (_isSubmitting.value) return
        _showArchiveDialog.value = false
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val result = ideaRepository.arquivar(ideaId)
                if (result.isSuccess) {
                    _actionMessage.value = "Ideia arquivada."
                    _archived.value = true
                } else {
                    _actionMessage.value = result.exceptionOrNull()?.toUserMessage()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionMessage.value = e.toUserMessage()
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun clearHistoryError() {
        _historyError.value = null
    }

    fun onDismissHistoryDialog() {
        _showHistoryDialog.value = false
    }
}
