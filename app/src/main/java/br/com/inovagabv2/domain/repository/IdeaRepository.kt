package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Priority
import kotlinx.coroutines.flow.Flow

interface IdeaRepository {
    fun getIdeas(): Flow<List<Idea>>
    fun getIdeasByAuthor(authorId: String): Flow<List<Idea>>
    fun getIdeaById(id: String): Flow<Idea?>
    suspend fun createIdea(idea: Idea): Result<Unit>
    suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Priority? = null): Result<Unit>
}
