package br.com.inovagabv2.presentation.leadership

import androidx.lifecycle.SavedStateHandle
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.StrategyStatus
import br.com.inovagabv2.domain.repository.StrategyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditStrategyViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeStrategyRepository
    private lateinit var viewModel: EditStrategyViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeStrategyRepository()
        val savedStateHandle = SavedStateHandle(mapOf("strategyId" to "est100"))
        viewModel = EditStrategyViewModel(fakeRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadStrategy loads strategy data from repository`() = runTest {
        val state = viewModel.state.first { !it.isLoading }

        assertEquals("est100", state.strategy?.id)
        assertEquals("Estratégia Teste", state.title)
        assertEquals(StrategyStatus.RASCUNHO, state.status)
    }

    @Test
    fun `onUpdateStrategy updates strategy successfully`() = runTest {
        viewModel.onTitleChange("Título Atualizado")
        viewModel.onUpdateStrategy()

        val state = viewModel.state.value

        assertEquals("Título Atualizado", state.title)
        assertEquals("Estratégia atualizada com sucesso.", state.feedbackMessage)
        assertFalse(state.isSaving)
    }

    @Test
    fun `onActivateStrategy activates strategy`() = runTest {
        viewModel.onActivateStrategy()

        val state = viewModel.state.value

        assertEquals(StrategyStatus.ATIVA, state.status)
        assertEquals("Estratégia ativada com sucesso.", state.feedbackMessage)
        assertFalse(state.isSaving)
    }

    @Test
    fun `onArchiveStrategy archives strategy and sets navigate back flag`() = runTest {
        viewModel.onShowArchiveDialog()
        viewModel.onArchiveStrategy()

        val state = viewModel.state.value

        assertEquals("Estratégia arquivada com sucesso.", state.feedbackMessage)
        assertTrue(state.shouldNavigateBack)
        assertFalse(state.isSaving)
    }

    private class FakeStrategyRepository : StrategyRepository {
        private var current = Strategy("est100", "Estratégia Teste", "Descrição", "2026-09-15", "Operação", "Campanha 1", StrategyStatus.RASCUNHO)

        override fun getStrategies(): Flow<List<Strategy>> = flowOf(listOf(current))
        override fun getStrategiesRemote(status: StrategyStatus?, categoria: String?, campanha: String?, pagina: Int, tamanho: Int): Flow<Pagina<Strategy>> = flowOf(Pagina(listOf(current), 0, 20, 1, 1, true, true))
        override fun getActiveStrategies(): Flow<List<Strategy>> = flowOf(listOf(current))
        override fun getStrategyById(id: String): Flow<Strategy?> = flowOf(current)

        override suspend fun createStrategy(titulo: String, descricao: String, data: String, categoria: String, campanha: String): Result<Strategy> = Result.success(current)
        override suspend fun createStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)

        override suspend fun updateStrategy(id: String, titulo: String, descricao: String, data: String, categoria: String, campanha: String): Result<Strategy> {
            current = current.copy(title = titulo, description = descricao, date = data, category = categoria, campaign = campanha)
            return Result.success(current)
        }

        override suspend fun updateStrategy(strategy: Strategy): Result<Unit> = Result.success(Unit)

        override suspend fun activateStrategy(id: String): Result<Strategy> {
            current = current.copy(status = StrategyStatus.ATIVA)
            return Result.success(current)
        }

        override suspend fun deactivateStrategy(id: String): Result<Strategy> {
            current = current.copy(status = StrategyStatus.INATIVA)
            return Result.success(current)
        }

        override suspend fun archiveStrategy(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun deleteStrategy(id: String): Result<Unit> = Result.success(Unit)
    }
}
