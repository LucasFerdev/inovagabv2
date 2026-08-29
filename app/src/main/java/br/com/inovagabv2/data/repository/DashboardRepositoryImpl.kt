package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.model.ProjectResult
import br.com.inovagabv2.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor() : DashboardRepository {

    private val _data = MutableStateFlow(DashboardData(
        roi = "24,8%",
        profit = "R$ 2,4 mi",
        costReduction = "R$ 1,1 mi",
        productivity = "+32%",
        activeProjectsCount = 18,
        approvedIdeasCount = 24,
        resultsByProject = listOf(
            ProjectResult("1", "Digitalização do Cartão de Embarque", "38%", "R$ 1,2 mi", "R$ 120 mil", "Alto"),
            ProjectResult("2", "Manutenção Inteligente (G8)", "25%", "R$ 950 mil", "R$ 250 mil", "Médio"),
            ProjectResult("3", "Telemetria e Redução de Consumo", "18%", "R$ 780 mil", "R$ 85 mil", "Médio")
        )
    ))

    override fun getDashboardData(): Flow<DashboardData> = _data
}
