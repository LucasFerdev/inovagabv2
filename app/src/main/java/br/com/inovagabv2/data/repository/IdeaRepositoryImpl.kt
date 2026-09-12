package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Priority
import br.com.inovagabv2.domain.repository.IdeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdeaRepositoryImpl @Inject constructor() : IdeaRepository {

    private val _ideas = MutableStateFlow<List<Idea>>(emptyList())

    override fun getIdeas(): Flow<List<Idea>> = _ideas

    override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> =
        _ideas.map { ideas -> ideas.filter { it.authorId == authorId } }

    override fun getIdeaById(id: String): Flow<Idea?> =
        _ideas.map { ideas -> ideas.find { it.id == id } }

    override suspend fun createIdea(idea: Idea): Result<Unit> {
        val currentList = _ideas.value.toMutableList()
        currentList.add(0, idea)
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
