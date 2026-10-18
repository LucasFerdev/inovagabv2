package br.com.inovagabv2.presentation.manager.ideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.toUserMessage
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.repository.IdeaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val state = combine(
        _allIdeas,
        _searchQuery,
        _selectedStatus,
        _isLoading,
        _error
    ) { ideas, query, status, isLoading, error ->
        val filtered = ideas.filter { idea ->
            (query.isEmpty() || idea.title.contains(query, ignoreCase = true) || idea.description.contains(query, ignoreCase = true)) &&
                (status == null || idea.status == status)
        }
        ManagerIdeasState(
            ideas = filtered,
            isLoading = isLoading,
            searchQuery = query,
            selectedStatus = status,
            error = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ManagerIdeasState(isLoading = true))

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (_allIdeas.value.isEmpty()) {
                _isLoading.value = true
            }
            _error.value = null
            try {
                ideaRepository.getIdeas().collect { ideas ->
                    _allIdeas.value = ideas
                    _isLoading.value = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.toUserMessage()
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
