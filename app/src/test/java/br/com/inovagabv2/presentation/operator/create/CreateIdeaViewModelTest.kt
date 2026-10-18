package br.com.inovagabv2.presentation.operator.create

import br.com.inovagabv2.core.util.NO_CONNECTION_MESSAGE
import br.com.inovagabv2.domain.model.HistoryItem
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.StrategyStatus
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.StrategyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CreateIdeaViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `IOException ao carregar estrategias vai para erro sem lancar`() = runTest {
        val viewModel = CreateIdeaViewModel(
            ideaRepository = FakeIdeaRepository(),
            strategyRepository = FakeStrategyRepository(error = IOException("falha de rede"))
        )

        val state = viewModel.state.value
        assertFalse(state.isLoadingStrategies)
        assertEquals(NO_CONNECTION_MESSAGE, state.strategiesError)
        assertEquals(emptyList<Strategy>(), state.activeStrategies)
    }

    private class FakeStrategyRepository(
        private val error: Throwable? = null
    ) : StrategyRepository {
        override fun getStrategies(): Flow<List<Strategy>> = flowOf(emptyList())
        override fun getStrategiesRemote(
            status: StrategyStatus?,
            categoria: String?,
            campanha: String?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Strategy>> = flowOf(Pagina(emptyList(), 0, 20, 0, 1, true, true))

        override fun getActiveStrategies(): Flow<List<Strategy>> = flow {
            error?.let { throw it }
            emit(emptyList())
        }

        override fun getStrategyById(id: String): Flow<Strategy?> = flowOf(null)
        override fun consultarHistorico(id: String): Flow<List<HistoryItem>> = flowOf(emptyList())
        override suspend fun createStrategy(
            titulo: String,
            descricao: String,
            data: String,
            categoria: String,
            campanha: String
        ): Result<Strategy> = Result.success(Strategy("1", titulo, descricao))

        override suspend fun createStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)
        override suspend fun updateStrategy(
            id: String,
            titulo: String,
            descricao: String,
            data: String,
            categoria: String,
            campanha: String
        ): Result<Strategy> = Result.success(Strategy(id, titulo, descricao))

        override suspend fun updateStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)
        override suspend fun activateStrategy(id: String): Result<Strategy> =
            Result.success(Strategy(id, "T", "D"))

        override suspend fun deactivateStrategy(id: String): Result<Strategy> =
            Result.success(Strategy(id, "T", "D"))

        override suspend fun archiveStrategy(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun deleteStrategy(id: String): Result<Unit> = Result.success(Unit)
    }

    private class FakeIdeaRepository : IdeaRepository {
        override fun getIdeas(): Flow<List<Idea>> = flowOf(emptyList())
        override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flowOf(emptyList())
        override fun getIdeasRemote(
            status: IdeaStatus?,
            categoria: String?,
            estrategiaId: String?,
            prioridade: Int?,
            pagina: Int,
            tamanho: Int
        ): Flow<Pagina<Idea>> = flowOf(Pagina(emptyList(), 0, 20, 0, 1, true, true))

        override fun getMyIdeasRemote(pagina: Int, tamanho: Int): Flow<Pagina<Idea>> =
            flowOf(Pagina(emptyList(), 0, 20, 0, 1, true, true))

        override fun getIdeaById(id: String): Flow<Idea?> = flowOf(null)
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

        override suspend fun analisar(id: String): Result<Idea> = Result.success(Idea(id, "T", category = "C"))
        override suspend fun priorizar(id: String, prioridade: Int, justificativa: String?): Result<Idea> =
            Result.success(Idea(id, "T", category = "C"))

        override suspend fun aprovar(id: String): Result<Idea> = Result.success(Idea(id, "T", category = "C"))
        override suspend fun rejeitar(id: String, justificativa: String): Result<Idea> =
            Result.success(Idea(id, "T", category = "C"))

        override suspend fun arquivar(id: String): Result<Unit> = Result.success(Unit)
    }
}
