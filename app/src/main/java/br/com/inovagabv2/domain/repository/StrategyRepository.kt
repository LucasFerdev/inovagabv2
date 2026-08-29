package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.Strategy
import kotlinx.coroutines.flow.Flow

interface StrategyRepository {
    fun getStrategies(): Flow<List<Strategy>>
    fun getStrategyById(id: String): Flow<Strategy?>
    suspend fun createStrategy(strategy: Strategy): Result<Unit>
    suspend fun updateStrategy(strategy: Strategy): Result<Unit>
    suspend fun deleteStrategy(id: String): Result<Unit>
}
