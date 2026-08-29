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
            title = "Otimização do embarque na Rodoviária",
            description = "Sugiro a criação de um sistema de QR Code para leitura rápida de passagens direto no embarque, reduzindo filas e tempo de parada dos ônibus.",
            authorId = "1",
            authorName = "João Silva",
            status = IdeaStatus.EM_ANALISE,
            priority = Priority.MEDIA,
            createdAt = "08/08/2026",
            benefits = "Redução do tempo de embarque em 20%, maior conforto para o passageiro, agilidade operacional.",
            category = "Operação",
            timeline = listOf(
                TimelineEvent("Enviada para análise", "08/08/2026 10:30", isCompleted = true),
                TimelineEvent("Recebida pelo Gestor", "09/08/2026 14:20", isCompleted = true),
                TimelineEvent("Aguardando decisão", "--", isCompleted = false)
            )
        ),
        Idea(
            id = "2",
            title = "Checklist Digital de Manutenção Preventiva",
            description = "Substituir o formulário de papel por um app onde o mecânico registra as condições do ônibus antes de cada viagem.",
            authorId = "1",
            authorName = "João Silva",
            status = IdeaStatus.ENVIADA,
            createdAt = "12/08/2026",
            benefits = "Redução de erros de preenchimento, histórico digital imediato, segurança da frota.",
            category = "Manutenção"
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
