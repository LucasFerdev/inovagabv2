package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectMetric
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
            name = "Digitalização do Cartão de Embarque",
            description = "Eliminar o uso de papel no embarque rodoviário através de leitura facial e QR Code.",
            status = ProjectStatus.EM_ANDAMENTO,
            progress = 0.62f,
            startDate = "10/05/2026",
            deadline = "10/12/2026",
            investment = "R$ 120.000",
            results = listOf(
                ProjectMetric("Redução de papel", "85%", "meta atingida"),
                ProjectMetric("Agilidade no embarque", "-30%", "vs. mês anterior"),
                ProjectMetric("Satisfação do passageiro", "+42%", "YTD")
            ),
            nextSteps = listOf("Treinamento de motoristas", "Instalação de tablets nas unidades", "Campanha com passageiros")
        ),
        Project(
            id = "2",
            name = "Manutenção Inteligente (Frota G8)",
            description = "Uso de sensores IOT para manutenção preditiva dos novos ônibus da frota.",
            status = ProjectStatus.EM_ANDAMENTO,
            progress = 0.48f,
            startDate = "15/04/2026",
            deadline = "15/09/2026",
            investment = "R$ 250.000"
        ),
        Project(
            id = "3",
            name = "Redução de Consumo (Telemetria)",
            description = "Novo sistema de monitoramento de condução para economia de combustível.",
            status = ProjectStatus.PLANEJADO,
            progress = 0.12f,
            startDate = "20/08/2026",
            deadline = "20/02/2027",
            investment = "R$ 85.000"
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
