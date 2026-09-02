package br.com.inovagabv2.presentation.manager.ideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerIdeasViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    private val _allIdeas = MutableStateFlow<List<Idea>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<IdeaStatus?>(null)

    val state = combine(_allIdeas, _searchQuery, _selectedStatus) { ideas, query, status ->
        val filtered = ideas.filter { idea ->
            (query.isEmpty() || idea.title.contains(query, ignoreCase = true) || idea.description.contains(query, ignoreCase = true)) &&
            (status == null || idea.status == status)
        }
        ManagerIdeasState(
            ideas = filtered,
            isLoading = false,
            searchQuery = query,
            selectedStatus = status
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ManagerIdeasState(isLoading = true))

    init {
        loadIdeas()
    }

    private fun loadIdeas() {
        viewModelScope.launch {
            ideaRepository.getIdeas().collect { ideas ->
                _allIdeas.value = ideas
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: IdeaStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }
}

data class ManagerIdeasState(
    val ideas: List<Idea> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedStatus: IdeaStatus? = null,
    val error: String? = null
)
