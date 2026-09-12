package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor() : ProjectRepository {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())

    override fun getProjects(): Flow<List<Project>> = _projects

    override fun getProjectById(id: String): Flow<Project?> = 
        _projects.map { projects -> projects.find { it.id == id } }

    override suspend fun createProject(project: Project): Result<Unit> {
        val currentList = _projects.value.toMutableList()
        currentList.add(0, project)
        _projects.value = currentList
        return Result.success(Unit)
    }

    override suspend fun updateProject(project: Project): Result<Unit> {
        val currentList = _projects.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == project.id }
        if (index != -1) {
            currentList[index] = project
            _projects.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Projeto não encontrado"))
    }
}
