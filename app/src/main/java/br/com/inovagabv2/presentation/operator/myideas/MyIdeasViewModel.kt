package br.com.inovagabv2.presentation.operator.myideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyIdeasViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<IdeaStatus?>(null)
    val selectedFilter: StateFlow<IdeaStatus?> = _selectedFilter.asStateFlow()

    private val _ideas = MutableStateFlow<List<Idea>>(emptyList())
    
    val filteredIdeas: StateFlow<List<Idea>> = combine(_ideas, _selectedFilter) { ideas, filter ->
        if (filter == null) ideas else ideas.filter { it.status == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadIdeas()
    }

    private fun loadIdeas() {
        viewModelScope.launch {
            val user = sessionManager.userSession.first()
            if (user != null) {
                ideaRepository.getIdeasByAuthor(user.id).collect {
                    _ideas.value = it
                }
            }
        }
    }

    fun onFilterSelected(status: IdeaStatus?) {
        _selectedFilter.value = status
    }
}
