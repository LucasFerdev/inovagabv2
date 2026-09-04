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
            ProjectResult("1", "Cartão de embarque digital", "32,5%", "R$ 81.250", "R$ 250.000", "Alto"),
            ProjectResult("2", "Manutenção inteligente", "28,9%", "R$ 52.020", "R$ 180.000", "Médio"),
            ProjectResult("3", "Telemetria e consumo", "18,7%", "R$ 4.675", "R$ 25.000", "Médio")
        )
    ))

    override fun getDashboardData(): Flow<DashboardData> = _data
}
