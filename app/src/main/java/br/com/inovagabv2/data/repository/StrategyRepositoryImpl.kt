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
            title = "Excelência na viagem",
            description = "Elevar a experiência do cliente em todas as etapas da jornada.",
            objectives = listOf("Reduzir atrasos em 15%", "Aumentar NPS de bordo"),
            isPublished = true,
            publishedDate = "15 abr 2026",
            category = "Experiência do cliente",
            campaign = "Jornada 2026",
            createdAt = "15/04/2026",
            updatedAt = "15/04/2026"
        ),
        Strategy(
            id = "2",
            title = "Eficiência energética",
            description = "Reduzir o consumo de combustível e promover o uso responsável dos recursos.",
            objectives = listOf("Reduzir consumo de diesel em 10%"),
            isPublished = true,
            publishedDate = "10 mar 2026",
            category = "Sustentabilidade",
            campaign = "Rota sustentável",
            createdAt = "10/03/2026",
            updatedAt = "10/03/2026"
        ),
        Strategy(
            id = "3",
            title = "Inovação com propósito",
            description = "Transformar ideias em soluções que geram valor para pessoas, clientes e para o futuro.",
            objectives = listOf("Inovação alinhada aos valores"),
            isPublished = true,
            publishedDate = "01 mar 2026",
            category = "Inovação",
            campaign = "Ideias que movem",
            createdAt = "01/03/2026",
            updatedAt = "01/03/2026"
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
