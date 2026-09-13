package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadershipProjectsViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _allProjects = MutableStateFlow<List<Project>>(emptyList())
    private val _selectedStatus = MutableStateFlow<ProjectStatus?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val projects: StateFlow<List<Project>> = combine(_allProjects, _selectedStatus, _searchQuery) { list, status, query ->
        list.filter { proj ->
            (status == null || proj.status == status) &&
            (query.isBlank() || proj.name.contains(query, ignoreCase = true) || proj.description.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalProjectsCount: StateFlow<Int> = _allProjects.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val delayedCount: StateFlow<Int> = _allProjects.map { list -> list.count { it.status == ProjectStatus.ATRASADO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageProgress: StateFlow<Int> = _allProjects.map { list ->
        if (list.isEmpty()) 0 else (list.map { it.progress }.average() * 100).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProjects().collect { list ->
                _allProjects.value = list
                _isLoading.value = false
            }
        }
    }

    fun onStatusFilterChange(status: ProjectStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
