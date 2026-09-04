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
            description = "Diretrizes para elevar a qualidade da experiência do cliente em todas as etapas da jornada.",
            objectives = listOf("Reduzir atrasos em 15%", "Aumentar NPS de bordo para 90", "Zero acidentes em rotas"),
            isPublished = true,
            publishedDate = "Publicada em 15/04/2025",
            createdAt = "15/04/2025",
            updatedAt = "15/04/2025"
        ),
        Strategy(
            id = "2",
            title = "Eficiência energética",
            description = "Orientações para reduzir o consumo de combustível e promover o uso responsável dos recursos.",
            objectives = listOf("Reduzir consumo de diesel em 10%", "Implementar reciclagem em 100% das unidades"),
            isPublished = true,
            publishedDate = "Publicada em 10/03/2025",
            createdAt = "10/03/2025",
            updatedAt = "10/03/2025"
        ),
        Strategy(
            id = "3",
            title = "Inovação com propósito",
            description = "Princípios para desenvolver soluções inovadoras alinhadas à estratégia e aos valores da Águia Branca.",
            objectives = listOf("Inovação alinhada aos valores"),
            isPublished = true,
            publishedDate = "Publicada em 01/03/2025",
            createdAt = "01/03/2025",
            updatedAt = "01/03/2025"
        ),
        Strategy(
            id = "4",
            title = "Segurança em primeiro lugar",
            description = "Diretrizes para garantir a segurança de passageiros, colaboradores e operações.",
            objectives = listOf("Segurança em 100% das operações"),
            isPublished = true,
            publishedDate = "Publicada em 20/02/2025",
            createdAt = "20/02/2025",
            updatedAt = "20/02/2025"
        ),
        Strategy(
            id = "5",
            title = "Sustentabilidade",
            description = "Compromissos e práticas para gerar impacto positivo no meio ambiente e na sociedade.",
            objectives = listOf("Impacto positivo no meio ambiente"),
            isPublished = true,
            publishedDate = "Publicada em 10/02/2025",
            createdAt = "10/02/2025",
            updatedAt = "10/02/2025"
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
