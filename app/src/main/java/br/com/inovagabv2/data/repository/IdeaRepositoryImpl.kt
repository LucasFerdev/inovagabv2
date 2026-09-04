package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Priority
import br.com.inovagabv2.domain.model.TimelineEvent
import br.com.inovagabv2.domain.repository.IdeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdeaRepositoryImpl @Inject constructor() : IdeaRepository {

    private val _ideas = MutableStateFlow<List<Idea>>(listOf(
        Idea(
            id = "1",
            title = "Otimização do embarque",
            description = "Proposta para reduzir o tempo de embarque nas rodoviárias por meio de organização de filas e sinalização.",
            authorId = "1",
            authorName = "João Silva",
            status = IdeaStatus.EM_ANALISE,
            priority = Priority.ALTA,
            createdAt = "20/05/2025",
            benefits = "Diminuição do tempo de espera, mais conforto para o cliente e melhor fluxo nas plataformas.",
            category = "Operação",
            timeline = listOf(
                TimelineEvent("Enviada para análise", "20/05/2025 09:30", isCompleted = true),
                TimelineEvent("Recebida pelo gestor", "20/05/2025 14:10", isCompleted = true),
                TimelineEvent("Aguardando decisão", "--", isCompleted = false)
            )
        ),
        Idea(
            id = "2",
            title = "Checklist digital de manutenção",
            description = "Digitalizar o checklist para mais agilidade e rastreabilidade.",
            authorId = "1",
            authorName = "João Silva",
            status = IdeaStatus.EM_ANALISE,
            priority = Priority.MEDIA,
            createdAt = "15/05/2025",
            benefits = "Mais agilidade e rastreabilidade na manutenção.",
            category = "Manutenção",
            timeline = listOf(
                TimelineEvent("Enviada para análise", "15/05/2025 10:00", isCompleted = true),
                TimelineEvent("Recebida pelo gestor", "15/05/2025 11:30", isCompleted = true),
                TimelineEvent("Aguardando decisão", "--", isCompleted = false)
            )
        ),
        Idea(
            id = "3",
            title = "Coleta seletiva nas garagens",
            description = "Implantar coleta seletiva para reduzir resíduos e impactos.",
            authorId = "1",
            authorName = "João Silva",
            status = IdeaStatus.APROVADA,
            priority = Priority.MEDIA,
            createdAt = "10/05/2025",
            benefits = "Redução de resíduos e impactos ambientais.",
            category = "Sustentabilidade",
            timeline = listOf(
                TimelineEvent("Enviada para análise", "10/05/2025 08:00", isCompleted = true),
                TimelineEvent("Aprovada pelo gestor", "12/05/2025 16:00", isCompleted = true)
            )
        ),
        Idea(
            id = "4",
            title = "Wi-Fi a bordo",
            description = "Disponibilizar Wi-Fi gratuito em toda a frota.",
            authorId = "104",
            authorName = "Beatriz Lima",
            status = IdeaStatus.ENVIADA,
            priority = Priority.BAIXA,
            createdAt = "05/05/2025",
            benefits = "Conectividade e satisfação do passageiro.",
            category = "Tecnologia"
        )
    ))

    override fun getIdeas(): Flow<List<Idea>> = _ideas

    override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = 
        _ideas.map { ideas -> ideas.filter { it.authorId == authorId } }

    override fun getIdeaById(id: String): Flow<Idea?> = 
        _ideas.map { ideas -> ideas.find { it.id == id } }

    override suspend fun createIdea(idea: Idea): Result<Unit> {
        val currentList = _ideas.value.toMutableList()
        currentList.add(idea)
        _ideas.value = currentList
        return Result.success(Unit)
    }

    override suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Priority?): Result<Unit> {
        val currentList = _ideas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val idea = currentList[index]
            currentList[index] = idea.copy(status = status, priority = priority ?: idea.priority)
            _ideas.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Ideia não encontrada"))
    }
}
