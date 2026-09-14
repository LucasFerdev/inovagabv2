package br.com.inovagabv2.presentation.manager.projects.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val isSaving: Boolean = false,
    val createdProject: Project? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val parsedDeadline: String? get() = parseDeadlineIso(deadline)
    val parsedInvestment: BigDecimal? get() = investmentStr.replace(",", ".").toBigDecimalOrNull()
    val isValid: Boolean
        get() = name.isNotBlank() &&
                description.isNotBlank() &&
                selectedStrategy != null &&
                parsedDeadline != null &&
                parsedInvestment != null
}

fun parseDeadlineIso(input: String): String? {
    val clean = input.trim()
    if (clean.isBlank()) return null

    val ddmmyyyyRegex = "^(\\d{2})/(\\d{2})/(\\d{4})$".toRegex()
    val ddmmyyyyMatch = ddmmyyyyRegex.matchEntire(clean)
    if (ddmmyyyyMatch != null) {
        val (day, month, year) = ddmmyyyyMatch.destructured
        val dayInt = day.toIntOrNull() ?: return null
        val monthInt = month.toIntOrNull() ?: return null
        val yearInt = year.toIntOrNull() ?: return null
        if (dayInt in 1..31 && monthInt in 1..12 && yearInt in 2020..2100) {
            return String.format(java.util.Locale.US, "%04d-%02d-%02d", yearInt, monthInt, dayInt)
        }
        return null
    }

    val yyyymmddRegex = "^(\\d{4})-(\\d{2})-(\\d{2})$".toRegex()
    val yyyymmddMatch = yyyymmddRegex.matchEntire(clean)
    if (yyyymmddMatch != null) {
        val (year, month, day) = yyyymmddMatch.destructured
        val dayInt = day.toIntOrNull() ?: return null
        val monthInt = month.toIntOrNull() ?: return null
        val yearInt = year.toIntOrNull() ?: return null
        if (dayInt in 1..31 && monthInt in 1..12 && yearInt in 2020..2100) {
            return String.format(java.util.Locale.US, "%04d-%02d-%02d", yearInt, monthInt, dayInt)
        }
        return null
    }

    return null
}

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
            _state.update { it.copy(isLoadingStrategies = true) }
            
            launch {
                strategyRepository.getActiveStrategies().collect { strats ->
                    val active = strats.filter { it.isPublished }
                    _state.update {
                        it.copy(
                            activeStrategies = active,
                            selectedStrategy = active.firstOrNull(),
                            isLoadingStrategies = false
                        )
                    }
                }
            }

            launch {
                ideaRepository.getIdeasRemote(status = IdeaStatus.APROVADA).collect { page ->
                    _state.update { it.copy(approvedIdeas = page.conteudo) }
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
    fun onDeadlineChange(v: String) = _state.update { it.copy(deadline = v) }

    fun submitProject() {
        val cur = _state.value
        val deadlineIso = cur.parsedDeadline
        if (deadlineIso == null) {
            _state.update { it.copy(errorMessage = "Informe uma data válida no formato DD/MM/AAAA ou AAAA-MM-DD (ex: 31/12/2026).") }
            return
        }

        val investBd = cur.parsedInvestment
        if (investBd == null) {
            _state.update { it.copy(errorMessage = "Informe um valor numérico válido para o investimento.") }
            return
        }

        if (!cur.isValid || cur.isSaving || cur.selectedStrategy == null) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

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
                        errorMessage = result.exceptionOrNull()?.message ?: "Erro ao criar projeto"
                    )
                }
            }
        }
    }
}
