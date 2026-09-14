package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.remote.api.InovaGabApi
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

class AiRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private lateinit var repository: AiRepositoryImpl
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

        repository = AiRepositoryImpl(api, json)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getIdeaAnalysis sends GET and returns mapped AiAnalysis on success`() = runTest {
        val responseJson = """
            {
              "ideiaId": "i100",
              "pontuacaoGeral": 95,
              "prioridadeSugerida": 5,
              "resumoExecutivo": "Excelente viabilidade técnica",
              "pontosFortes": ["Alto ROI", "Fácil adoção"],
              "riscos": ["Ajustes iniciais"],
              "recomendacoes": ["Realizar piloto"],
              "modelo": "gemini-1.5-flash",
              "geradoEm": "2026-09-13T20:00:00Z",
              "aviso": null
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = repository.getIdeaAnalysis("i100")

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ia/ideias/i100/analise", recordedRequest.path)
        assertEquals("GET", recordedRequest.method)

        assertTrue(result.isSuccess)
        val analysis = result.getOrNull()!!
        assertEquals("i100", analysis.ideaId)
        assertEquals(95, analysis.overallScore)
        assertEquals(5, analysis.suggestedPriority)
        assertEquals(2, analysis.strengths.size)
        assertEquals("gemini-1.5-flash", analysis.model)
    }

    @Test
    fun `analyzeIdea sends POST with recalcular false`() = runTest {
        val responseJson = """
            {
              "ideiaId": "i100",
              "pontuacaoGeral": 88,
              "prioridadeSugerida": 4,
              "resumoExecutivo": "Boa proposta",
              "pontosFortes": [],
              "riscos": [],
              "recomendacoes": [],
              "modelo": "gemini-1.5-flash",
              "geradoEm": "2026-09-13T20:00:00Z"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = repository.analyzeIdea("i100", recalculate = false)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ia/ideias/i100/analisar?recalcular=false", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)

        assertTrue(result.isSuccess)
        assertEquals(88, result.getOrNull()?.overallScore)
    }

    @Test
    fun `analyzeIdea sends POST with recalcular true`() = runTest {
        val responseJson = """
            {
              "ideiaId": "i100",
              "pontuacaoGeral": 90,
              "prioridadeSugerida": 4,
              "resumoExecutivo": "Recalculado",
              "pontosFortes": [],
              "riscos": [],
              "recomendacoes": [],
              "modelo": "gemini-1.5-flash",
              "geradoEm": "2026-09-13T20:00:00Z"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = repository.analyzeIdea("i100", recalculate = true)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ia/ideias/i100/analisar?recalcular=true", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `getIdeaAnalysis handles 404 No Analysis error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val result = repository.getIdeaAnalysis("i100")

        assertTrue(result.isFailure)
        assertEquals("Esta ideia ainda não possui análise da IA.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `analyzeIdea handles 429 Rate Limit error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(429))

        val result = repository.analyzeIdea("i100")

        assertTrue(result.isFailure)
        assertEquals("O limite temporário da IA foi atingido. Tente novamente mais tarde.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `analyzeIdea handles 503 Service Unavailable error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(503))

        val result = repository.analyzeIdea("i100")

        assertTrue(result.isFailure)
        assertEquals("O serviço de IA está indisponível no momento.", result.exceptionOrNull()?.message)
    }
}
