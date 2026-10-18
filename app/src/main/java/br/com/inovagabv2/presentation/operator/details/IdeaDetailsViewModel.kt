package br.com.inovagabv2.presentation.operator.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.LOAD_FAILED_MESSAGE
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.AiAnalysis
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
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

    fun clearHistoryError() {
        _historyError.value = null
    }

    fun onDismissHistoryDialog() {
        _showHistoryDialog.value = false
    }
}
