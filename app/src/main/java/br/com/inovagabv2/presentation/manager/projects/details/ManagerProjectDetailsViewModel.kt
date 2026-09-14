package br.com.inovagabv2.presentation.manager.projects.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class ManagerProjectDetailsState(
    val project: Project? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null,
    val showProgressDialog: Boolean = false,
    val showResultsDialog: Boolean = false,
    val showConcludeDialog: Boolean = false,
    val showCancelDialog: Boolean = false,
    val shouldNavigateBack: Boolean = false
)

@HiltViewModel
class ManagerProjectDetailsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _state = MutableStateFlow(ManagerProjectDetailsState())
    val state: StateFlow<ManagerProjectDetailsState> = _state.asStateFlow()

    init {
        loadProject()
    }

    fun loadProject() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            projectRepository.getProjectById(projectId).collect { project ->
                _state.update { it.copy(project = project, isLoading = false) }
            }
        }
    }

    fun onShowProgressDialog() = _state.update { it.copy(showProgressDialog = true) }
    fun onDismissProgressDialog() = _state.update { it.copy(showProgressDialog = false) }

    fun onUpdateProgress(
        etapa: ProjectStage,
        status: ProjectStatus,
        percentualProgresso: Int,
        justificativa: String? = null
    ) {
        val current = _state.value.project ?: return
        if (_state.value.isSubmitting) return

        if (percentualProgresso < current.percentualProgresso && justificativa.isNullOrBlank()) {
            _state.update { it.copy(errorMessage = "Justificativa obrigatória ao reduzir o progresso.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, showProgressDialog = false, errorMessage = null) }
            val result = projectRepository.updateProgress(
                id = projectId,
                etapa = etapa,
                status = status,
                percentualProgresso = percentualProgresso,
                justificativa = justificativa
            )
            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        project = result.getOrNull(),
                        isSubmitting = false,
                        feedbackMessage = "Progresso do projeto atualizado com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Erro ao atualizar progresso"
                    )
                }
            }
        }
    }

    fun onShowResultsDialog() = _state.update { it.copy(showResultsDialog = true) }
    fun onDismissResultsDialog() = _state.update { it.copy(showResultsDialog = false) }

    fun onRegisterResults(
        retornoFinanceiro: BigDecimal,
        ganhoProdutividadePercentual: BigDecimal,
        resultado: String
    ) {
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, showResultsDialog = false, errorMessage = null) }
            val res = projectRepository.registerResults(
                id = projectId,
                retornoFinanceiro = retornoFinanceiro,
                ganhoProdutividadePercentual = ganhoProdutividadePercentual,
                resultado = resultado
            )
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        project = res.getOrNull(),
                        isSubmitting = false,
                        feedbackMessage = "Resultados registrados com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao registrar resultados"
                    )
                }
            }
        }
    }

    fun onShowConcludeDialog() = _state.update { it.copy(showConcludeDialog = true) }
    fun onDismissConcludeDialog() = _state.update { it.copy(showConcludeDialog = false) }

    fun onConcludeProject() {
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, showConcludeDialog = false, errorMessage = null) }
            val res = projectRepository.concludeProject(projectId)
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        project = res.getOrNull(),
                        isSubmitting = false,
                        feedbackMessage = "Projeto concluído com sucesso."
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao concluir projeto"
                    )
                }
            }
        }
    }

    fun onShowCancelDialog() = _state.update { it.copy(showCancelDialog = true) }
    fun onDismissCancelDialog() = _state.update { it.copy(showCancelDialog = false) }

    fun onCancelProject() {
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, showCancelDialog = false, errorMessage = null) }
            val res = projectRepository.cancelProject(projectId)
            if (res.isSuccess) {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        feedbackMessage = "Projeto cancelado com sucesso.",
                        shouldNavigateBack = true
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = res.exceptionOrNull()?.message ?: "Erro ao cancelar projeto"
                    )
                }
            }
        }
    }

    fun clearFeedbackMessage() = _state.update { it.copy(feedbackMessage = null) }
    fun clearErrorMessage() = _state.update { it.copy(errorMessage = null) }
}
