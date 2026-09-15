package br.com.inovagabv2.presentation

import br.com.inovagabv2.data.mapper.formatActionName
import br.com.inovagabv2.data.mapper.formatStatusName
import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.remote.dto.idea.HistoricoIdeiaResponseDto
import br.com.inovagabv2.data.remote.dto.project.HistoricoProjetoResponseDto
import br.com.inovagabv2.data.remote.dto.strategy.HistoricoEstrategiaResponseDto
import br.com.inovagabv2.data.repository.IdeaRepositoryImpl
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(InovaGabApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        Dispatchers.resetMain()
    }

    @Test
    fun `ANALISADA_POR_IA is formatted as Analisada por IA`() {
        assertEquals("Analisada por IA", formatActionName("ANALISADA_POR_IA"))
        assertEquals("Analisada por IA", formatActionName("ANALISADA_IA"))
    }

    @Test
    fun `unknown future action is formatted safely without throwing exception`() {
        assertEquals("Nova acao futura", formatActionName("NOVA_ACAO_FUTURA"))
        assertEquals("Acao desconhecida", formatActionName("ACAO_DESCONHECIDA"))
    }

    @Test
    fun `idea history DTO with ANALISADA_POR_IA is deserialized and mapped correctly`() = runTest {
        val jsonHistory = """
            [
              {"id": "h1", "acao": "CRIADA", "dataHora": "2026-09-14T08:00:00Z", "status": "ENVIADA"},
              {"id": "h2", "acao": "ANALISADA", "dataHora": "2026-09-14T09:00:00Z", "status": "EM_ANALISE"},
              {"id": "h3", "acao": "ANALISADA_POR_IA", "dataHora": "2026-09-14T10:00:00Z", "status": "EM_ANALISE"},
              {"id": "h4", "acao": "NOVA_ACAO_SUBSEQUENTE", "dataHora": "2026-09-14T11:00:00Z", "status": "APROVADA"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonHistory))

        val repo = IdeaRepositoryImpl(api, json)
        val historyList = repo.consultarHistorico("i100").first()

        assertEquals(4, historyList.size)
        assertEquals("Criada", historyList[0].action)
        assertEquals("Analisada", historyList[1].action)
        assertEquals("Analisada por IA", historyList[2].action)
        assertEquals("Nova acao subsequente", historyList[3].action)
    }

    @Test
    fun `strategy history DTO handles unknown future action safely`() {
        val dto = HistoricoEstrategiaResponseDto(
            id = "h1",
            acao = "NOVA_ETAPA_LIDERANCA",
            dataHora = "2026-09-14T12:00:00Z"
        )
        val domain = dto.toDomain()
        assertEquals("Nova etapa lideranca", domain.action)
    }

    @Test
    fun `project history DTO handles unknown future action safely`() {
        val dto = HistoricoProjetoResponseDto(
            id = "h1",
            acao = "PROGRESSO_ACELERADO",
            dataHora = "2026-09-14T13:00:00Z"
        )
        val domain = dto.toDomain()
        assertEquals("Progresso acelerado", domain.action)
    }

    @Test
    fun `formatStatusName maps known statuses and handles unknown status strings safely`() {
        assertEquals("Em análise", formatStatusName("EM_ANALISE"))
        assertEquals("Em andamento", formatStatusName("EM_ANDAMENTO"))
        assertEquals("Novo status futuro", formatStatusName("NOVO_STATUS_FUTURO"))
    }
}
