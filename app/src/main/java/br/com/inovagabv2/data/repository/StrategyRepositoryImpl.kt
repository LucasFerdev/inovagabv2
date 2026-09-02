package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.repository.StrategyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StrategyRepositoryImpl @Inject constructor() : StrategyRepository {

    private val _strategies = MutableStateFlow<List<Strategy>>(listOf(
        Strategy(
            id = "1",
            title = "Excelência na Viagem",
            description = "Garantir a melhor experiência de transporte rodoviário do Brasil, focando em pontualidade e conforto.",
            objectives = listOf("Reduzir atrasos em 15%", "Aumentar NPS de bordo para 90", "Zero acidentes em rotas"),
            createdAt = "01/01/2026",
            updatedAt = "01/01/2026"
        ),
        Strategy(
            id = "2",
            title = "Eficiência Energética e Sustentabilidade",
            description = "Reduzir o impacto ambiental da nossa frota através de tecnologia e direção defensiva.",
            objectives = listOf("Reduzir consumo de diesel em 10%", "Implementar reciclagem em 100% das unidades"),
            createdAt = "15/01/2026",
            updatedAt = "15/01/2026"
        ),
        Strategy(
            id = "3",
            title = "Inovação no Atendimento",
            description = "Facilitar a vida do passageiro desde a compra da passagem até o desembarque final.",
            objectives = listOf("Digitalizar 100% dos cartões de embarque", "Reduzir tempo de guichê em 30%"),
            createdAt = "20/01/2026",
            updatedAt = "20/01/2026"
        )
    ))

    override fun getStrategies(): Flow<List<Strategy>> = _strategies

    override fun getStrategyById(id: String): Flow<Strategy?> = 
        _strategies.map { strategies -> strategies.find { it.id == id } }

    override suspend fun createStrategy(strategy: Strategy): Result<Unit> {
        val currentList = _strategies.value.toMutableList()
        currentList.add(strategy)
        _strategies.value = currentList
        return Result.success(Unit)
    }

    override suspend fun updateStrategy(strategy: Strategy): Result<Unit> {
        val currentList = _strategies.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == strategy.id }
        if (index != -1) {
            currentList[index] = strategy
            _strategies.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Estratégia não encontrada"))
    }

    override suspend fun deleteStrategy(id: String): Result<Unit> {
        val currentList = _strategies.value.toMutableList()
        if (currentList.removeIf { it.id == id }) {
            _strategies.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Estratégia não encontrada"))
    }
}
