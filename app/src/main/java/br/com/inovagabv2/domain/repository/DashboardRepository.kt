package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.model.DashboardProjectDetails
import br.com.inovagabv2.domain.model.DashboardStrategyDetails
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardData(): Flow<DashboardData>
    fun getDashboardStrategyDetails(strategyId: String): Flow<DashboardStrategyDetails?>
    fun getDashboardProjectDetails(projectId: String): Flow<DashboardProjectDetails?>
}
