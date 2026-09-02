package br.com.inovagabv2.presentation.operator.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeaDetailsViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ideaId: String = checkNotNull(savedStateHandle["ideaId"])

    private val _idea = MutableStateFlow<Idea?>(null)
    val idea: StateFlow<Idea?> = _idea.asStateFlow()

    init {
        loadIdea()
    }

    private fun loadIdea() {
        viewModelScope.launch {
            ideaRepository.getIdeaById(ideaId).collect {
                _idea.value = it
            }
        }
    }
}
