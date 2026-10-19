package br.com.inovagabv2.presentation.manager.projects.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.dateFieldError
import br.com.inovagabv2.core.util.parseDateBr
import br.com.inovagabv2.core.util.parseDecimalBr
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class ManagerCreateProjectState(
    val name: String = "",
    val description: String = "",
    val activeStrategies: List<Strategy> = emptyList(),
    val selectedStrategy: Strategy? = null,
    val approvedIdeas: List<Idea> = emptyList(),
    val selectedIdea: Idea? = null,
    val investmentStr: String = "",
    val deadline: String = "",
    val isLoadingStrategies: Boolean = true,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val createdProject: Project? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val parsedDeadline: String? get() = parseDeadlineIso(deadline)
    val deadlineError: String? get() = dateFieldError(deadline)
    val parsedInvestment: BigDecimal? get() = parseDecimalBr(investmentStr)
    val isValid: Boolean
        get() = name.isNotBlank() &&
                description.isNotBlank() &&
                selectedStrategy != null &&
                parsedDeadline != null &&
                parsedInvestment != null
}

fun parseDeadlineIso(input: String): String? = parseDateBr(input)?.toString()

@HiltViewModel
class ManagerCreateProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val strategyRepository: StrategyRepository,
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ManagerCreateProjectState())
    val state: StateFlow<ManagerCreateProjectState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingStrategies = true, loadError = null) }

            launch {
                try {
                    strategyRepository.getActiveStrategies()
                        .catch { e ->
                            if (e is CancellationException) throw e
                            _state.update { it.copy(isLoadingStrategies = false, loadError = e.toUserMessage()) }
                        }
                        .collect { strats ->
                            val active = strats.filter { it.isPublished }
                            _state.update {
                                it.copy(
                                    activeStrategies = active,
                                    selectedStrategy = active.firstOrNull(),
                                    isLoadingStrategies = false,
                                    loadError = null
                                )
                            }
                        }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _state.update { it.copy(isLoadingStrategies = false, loadError = e.toUserMessage()) }
                }
            }

            launch {
                try {
                    ideaRepository.getIdeasRemote(status = IdeaStatus.APROVADA)
                        .catch { e ->
                            if (e is CancellationException) throw e
                            _state.update { it.copy(errorMessage = e.toUserMessage()) }
                        }
                        .collect { page ->
                            _state.update { it.copy(approvedIdeas = page.conteudo) }
                        }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.toUserMessage()) }
                }
            }
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v) }
    fun onDescriptionChange(v: String) = _state.update { it.copy(description = v) }
    fun onStrategySelected(strategy: Strategy) {
        _state.update {
            it.copy(
                selectedStrategy = strategy,
                selectedIdea = null
            )
        }
    }
    fun onIdeaSelected(idea: Idea?) = _state.update { it.copy(selectedIdea = idea) }
    fun onInvestmentChange(v: String) = _state.update { it.copy(investmentStr = v) }
    fun onDeadlineChange(v: String) = _state.update { it.copy(deadline = v.filter(Char::isDigit).take(8)) }

    fun submitProject() {
        val cur = _state.value
        val deadlineIso = cur.parsedDeadline ?: return

        val investBd = cur.parsedInvestment
        if (investBd == null) {
            _state.update { it.copy(errorMessage = "Informe um valor numérico válido para o investimento.") }
            return
        }

        if (!cur.isValid || cur.isSaving || cur.selectedStrategy == null) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val result = projectRepository.createProject(
                    nome = cur.name,
                    descricao = cur.description,
                    estrategiaId = cur.selectedStrategy.id,
                    ideiaOrigemId = cur.selectedIdea?.id,
                    investimento = investBd,
                    prazo = deadlineIso
                )

                if (result.isSuccess) {
                    _state.update {
                        it.copy(
                            createdProject = result.getOrNull(),
                            isSaving = false,
                            isSuccess = true
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.exceptionOrNull()?.toUserMessage()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.toUserMessage()) }
            }
        }
    }
}
