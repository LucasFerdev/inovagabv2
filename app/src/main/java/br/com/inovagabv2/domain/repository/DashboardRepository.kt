package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.DashboardData
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardData(): Flow<DashboardData>
}
