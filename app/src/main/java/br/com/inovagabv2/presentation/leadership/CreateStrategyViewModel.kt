package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.repository.StrategyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateStrategyViewModel @Inject constructor(
    private val repository: StrategyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateStrategyState())
    val state = _state.asStateFlow()

    fun onTitleChange(title: String) = _state.update { it.copy(title = title) }
    fun onDescriptionChange(desc: String) = _state.update { it.copy(description = desc) }
    fun onDateChange(date: String) = _state.update { it.copy(date = date) }
    fun onCategoryChange(cat: String) = _state.update { it.copy(category = cat) }
    fun onCampaignChange(camp: String) = _state.update { it.copy(campaign = camp) }

    fun saveStrategy(activateNow: Boolean = false) {
        val current = _state.value
        if (!current.isValid || current.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val dateVal = current.date.ifBlank { "2026-09-15" }

            val result = repository.createStrategy(
                titulo = current.title,
                descricao = current.description,
                data = dateVal,
                categoria = current.category,
                campanha = current.campaign
            )

            if (result.isSuccess) {
                val created = result.getOrNull()
                if (activateNow && created != null) {
                    repository.activateStrategy(created.id)
                }
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = result.exceptionOrNull()?.message ?: "Erro ao criar estratégia"
                    )
                }
            }
        }
    }
}

data class CreateStrategyState(
    val title: String = "",
    val description: String = "",
    val date: String = "2026-09-15",
    val category: String = "Operação",
    val campaign: String = "Inovação 2026",
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean get() = title.isNotBlank() && description.isNotBlank() && category.isNotBlank() && campaign.isNotBlank()
}
