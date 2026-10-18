package br.com.inovagabv2.presentation.manager.ideas

import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.repository.IdeaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManagerIdeasViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh busca ideias de novo`() = runTest {
        val repository = FakeIdeaRepository()
        repository.ideas = listOf(Idea("1", "Ideia antiga", category = "Operação"))

        val viewModel = ManagerIdeasViewModel(repository)
        val initial = viewModel.state.first { !it.isLoading && it.error == null }

        assertEquals(1, repository.calls)
        assertEquals("Ideia antiga", initial.ideas.single().title)

        repository.ideas = listOf(Idea("2", "Ideia nova", category = "Operação"))
        viewModel.refresh()
        val refreshed = viewModel.state.first { !it.isLoading && it.ideas.any { idea -> idea.id == "2" } }

        assertEquals(2, repository.calls)
        assertEquals("Ideia nova", refreshed.ideas.single().title)
        assertNull(refreshed.error)
    }

    @Test
    fun `falha ao carregar ideias preenche o erro sem derrubar`() = runTest {
        val repository = FakeIdeaRepository()
        repository.failure = IllegalStateException("falha de rede")

        val viewModel = ManagerIdeasViewModel(repository)
        val state = viewModel.state.first { !it.isLoading }

        assertEquals("falha de rede", state.error)
        assertEquals(true, state.ideas.isEmpty())
    }

    private class FakeIdeaRepository : IdeaRepository {
        var calls = 0
        var ideas: List<Idea> = emptyList()
        var failure: Throwable? = null

        override fun getIdeas(): Flow<List<Idea>> = flow {
            calls++
            failure?.let { throw it }
            emit(ideas)
        }

        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = getIdeas()
        override fun getIdeasRemote(
            status: IdeaStatus?,
            categoria: String?,
            estrategiaId: String?,
            prioridade: Int?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Idea>> = flowOf(Pagina(ideas, 0, 20, ideas.size.toLong(), 1, true, true))

        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> =
            flowOf(Pagina(ideas, 0, 20, ideas.size.toLong(), 1, true, true))

        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(ideas.find { it.id == id })
        override fun consultarHistorico(id: String): Flow<List<HistoryItem>> = flowOf(emptyList())

        override suspend fun createIdea(
            titulo: String,
            problema: String,
            solucaoProposta: String,
            beneficiosEsperados: String,
            categoria: String,
            estrategiaId: String
        ): Result<Idea> = Result.success(Idea("1", titulo, category = categoria))

        override suspend fun createIdea(idea: Idea): Result<Unit> = Result.success(Unit)
        override suspend fun updateIdeaStatus(
            id: String,
            status: IdeaStatus,
            priority: Int?,
            justificativa: String?
        ): Result<Unit> = Result.success(Unit)

        override suspend fun analisar(id: String): Result<Idea> = Result.success(Idea("1", "T", category = "C"))
        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> =
            Result.success(Idea("1", "T", category = "C"))

        override suspend fun aprovar(id: String): Result<Idea> = Result.success(Idea("1", "T", category = "C"))
        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> =
            Result.success(Idea("1", "T", category = "C"))

        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }
}
