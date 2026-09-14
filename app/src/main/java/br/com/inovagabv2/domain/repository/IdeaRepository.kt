package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import kotlinx.coroutines.flow.Flow

interface IdeaRepository {
    fun getIdeas(): Flow<List<Idea>>
    fun getIdeasByAuthor(authorId: String): Flow<List<Idea>>
    fun getIdeasRemote(
        status: IdeaStatus? = null,
        categoria: String? = null,
        estrategiaId: String? = null,
        prioridade: Int? = null,
        pagina: Int = 0,
        tamanho: Int = 20
    ): Flow<Pagina<Idea>>
    fun getMyIdeasRemote(pagina: Int = 0, tamanho: Int = 20): Flow<Pagina<Idea>>
    fun getIdeaById(id: String): Flow<Idea?>
    suspend fun createIdea(
        titulo: String,
        problema: String,
        solucaoProposta: String,
        beneficiosEsperados: String,
        categoria: String,
        estrategiaId: String
    ): Result<Idea>
    suspend fun createIdea(idea: Idea): Result<Unit>
    suspend fun updateIdeaStatus(id: String, status: IdeaStatus, priority: Int? = null, justificativa: String? = null): Result<Unit>
    suspend fun analisar(id: String): Result<Idea>
    suspend fun priorizar(id: String, prioridade: Int, justificativa: String? = null): Result<Idea>
    suspend fun aprovar(id: String): Result<Idea>
    suspend fun rejeitar(id: String, justificativa: String): Result<Idea>
    suspend fun arquivar(id: String): Result<Unit>
}
