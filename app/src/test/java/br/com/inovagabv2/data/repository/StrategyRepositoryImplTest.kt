package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.domain.model.StrategyStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class StrategyRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private lateinit var repository: StrategyRepositoryImpl
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(InovaGabApi::class.java)

        repository = StrategyRepositoryImpl(api, json)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `createStrategy sends POST and maps created Strategy`() = runTest {
        val responseJson = """
            {
              "id": "est123",
              "titulo": "Estratégia 1",
              "descricao": "Descrição 1",
              "data": "2026-09-15",
              "categoria": "Operação",
              "campanha": "Jornada 2026",
              "status": "RASCUNHO",
              "criadoPorId": "u1",
              "atualizadoPorId": "u1"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(responseJson))

        val result = repository.createStrategy(
            titulo = "Estratégia 1",
            descricao = "Descrição 1",
            data = "2026-09-15",
            categoria = "Operação",
            campanha = "Jornada 2026"
        )

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/estrategias", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)

        assertTrue(result.isSuccess)
        val strategy = result.getOrNull()!!
        assertEquals("est123", strategy.id)
        assertEquals(StrategyStatus.RASCUNHO, strategy.status)
    }

    @Test
    fun `getActiveStrategies calls ativas endpoint and maps list`() = runTest {
        val responseJson = """
            [
              {
                "id": "est1",
                "titulo": "Ativa 1",
                "descricao": "Desc",
                "data": "2026-09-15",
                "categoria": "Cat",
                "campanha": "Camp",
                "status": "ATIVA",
                "criadoPorId": "u1",
                "atualizadoPorId": "u1"
              }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val strategies = repository.getActiveStrategies().first()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/estrategias/ativas", recordedRequest.path)

        assertEquals(1, strategies.size)
        assertEquals("Ativa 1", strategies.first().title)
        assertTrue(strategies.first().isPublished)
    }

    @Test
    fun `activateStrategy calls PATCH ativar endpoint`() = runTest {
        val responseJson = """
            {
              "id": "est1",
              "titulo": "Ativa",
              "descricao": "D",
              "data": "2026-09-15",
              "categoria": "C",
              "campanha": "Ca",
              "status": "ATIVA",
              "criadoPorId": "u1",
              "atualizadoPorId": "u1"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = repository.activateStrategy("est1")

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/estrategias/est1/ativar", recordedRequest.path)
        assertEquals("PATCH", recordedRequest.method)

        assertTrue(result.isSuccess)
        assertEquals(StrategyStatus.ATIVA, result.getOrNull()?.status)
    }

    @Test
    fun `archiveStrategy handles 204 No Content`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val result = repository.archiveStrategy("est1")

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/estrategias/est1", recordedRequest.path)
        assertEquals("DELETE", recordedRequest.method)

        assertTrue(result.isSuccess)
    }
}
