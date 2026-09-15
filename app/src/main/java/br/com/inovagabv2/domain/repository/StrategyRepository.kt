package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.StrategyStatus
import kotlinx.coroutines.flow.Flow

interface StrategyRepository {
    fun getStrategies(): Flow<List<Strategy>>
    fun getStrategiesRemote(
        status: StrategyStatus? = null,
        categoria: String? = null,
        campanha: String? = null,
        pagina: Int = 0,
        tamanho: Int = 20
    ): Flow<Pagina<Strategy>>
    fun getActiveStrategies(): Flow<List<Strategy>>
    fun getStrategyById(id: String): Flow<Strategy?>
    fun consultarHistorico(id: String): Flow<List<HistoryItem>>
    suspend fun createStrategy(
        titulo: String,
        descricao: String,
        data: String,
        categoria: String,
        campanha: String
    ): Result<Strategy>
    suspend fun createStrategy(strategy: Strategy): Result<Unit>
    suspend fun updateStrategy(
        id: String,
        titulo: String,
        descricao: String,
        data: String,
        categoria: String,
        campanha: String
    ): Result<Strategy>
    suspend fun updateStrategy(strategy: Strategy): Result<Unit>
    suspend fun activateStrategy(id: String): Result<Strategy>
    suspend fun deactivateStrategy(id: String): Result<Strategy>
    suspend fun archiveStrategy(id: String): Result<Unit>
    suspend fun deleteStrategy(id: String): Result<Unit>
}
