package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor() : ProjectRepository {

    private val _projects = MutableStateFlow<List<Project>>(listOf(
        Project(
            id = "1",
            name = "Cartão de embarque digital",
            description = "Eliminar o uso de papel no embarque rodoviário através de leitura facial e QR Code.",
            status = ProjectStatus.EM_ANDAMENTO,
            progress = 0.65f,
            startDate = "10/05/2025",
            deadline = "30/09/2025",
            investment = "R$ 250.000",
            authorName = "Lucas Almeida"
        ),
        Project(
            id = "2",
            name = "Manutenção inteligente",
            description = "Uso de sensores IOT para manutenção preditiva dos ônibus da frota.",
            status = ProjectStatus.EM_ANDAMENTO,
            progress = 0.40f,
            startDate = "15/04/2025",
            deadline = "15/08/2025",
            investment = "R$ 180.000",
            authorName = "Juliana Martins"
        ),
        Project(
            id = "3",
            name = "Wi-Fi a bordo",
            description = "Disponibilizar Wi-Fi gratuito em toda a frota.",
            status = ProjectStatus.PLANEJADO,
            progress = 0.15f,
            startDate = "20/08/2025",
            deadline = "20/11/2025",
            investment = "R$ 320.000",
            authorName = "Beatriz Lima"
        )
    ))

    override fun getProjects(): Flow<List<Project>> = _projects

    override fun getProjectById(id: String): Flow<Project?> = 
        _projects.map { projects -> projects.find { it.id == id } }

    override suspend fun createProject(project: Project): Result<Unit> {
        val currentList = _projects.value.toMutableList()
        currentList.add(project)
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
