package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>
    fun getProjectById(id: String): Flow<Project?>
    suspend fun createProject(project: Project): Result<Unit>
    suspend fun updateProject(project: Project): Result<Unit>
}
