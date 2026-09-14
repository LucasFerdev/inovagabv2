package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.domain.model.IdeaStatus
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

class IdeaRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private lateinit var repository: IdeaRepositoryImpl
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

        repository = IdeaRepositoryImpl(api, json)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `createIdea sends POST and returns mapped Idea on success`() = runTest {
        val responseJson = """
            {
              "id": "id123",
              "titulo": "Título Teste",
              "problema": "Problema Teste",
              "solucaoProposta": "Solução Teste",
              "beneficiosEsperados": "Benefícios Teste",
              "categoria": "Operação",
              "estrategiaId": "est123",
              "autorId": "user456",
              "status": "ENVIADA"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(responseJson))

        val result = repository.createIdea(
            titulo = "Título Teste",
            problema = "Problema Teste",
            solucaoProposta = "Solução Teste",
            beneficiosEsperados = "Benefícios Teste",
            categoria = "Operação",
            estrategiaId = "est123"
        )

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ideias", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)

        assertTrue(result.isSuccess)
        val idea = result.getOrNull()!!
        assertEquals("id123", idea.id)
        assertEquals("Título Teste", idea.title)
        assertEquals(IdeaStatus.ENVIADA, idea.status)
    }

    @Test
    fun `getMyIdeasRemote calls minhas ideas endpoint and maps page`() = runTest {
        val pageJson = """
            {
              "conteudo": [
                {
                  "id": "id1",
                  "titulo": "Minha Ideia",
                  "problema": "Prob",
                  "solucaoProposta": "Sol",
                  "beneficiosEsperados": "Ben",
                  "categoria": "Cat",
                  "estrategiaId": "est1",
                  "autorId": "me",
                  "status": "ENVIADA"
                }
              ],
              "pagina": 0,
              "tamanho": 20,
              "totalElementos": 1,
              "totalPaginas": 1,
              "primeira": true,
              "ultima": true
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(pageJson))

        val page = repository.getMyIdeasRemote(0, 20).first()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ideias/minhas?pagina=0&tamanho=20", recordedRequest.path)

        assertEquals(1, page.conteudo.size)
        assertEquals("Minha Ideia", page.conteudo.first().title)
        assertEquals(1L, page.totalElementos)
    }

    @Test
    fun `updateIdeaStatus aprovar calls aprovar endpoint`() = runTest {
        val responseJson = """
            {
              "id": "id1",
              "titulo": "T",
              "problema": "P",
              "solucaoProposta": "S",
              "beneficiosEsperados": "B",
              "categoria": "C",
              "estrategiaId": "e",
              "autorId": "a",
              "status": "APROVADA"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = repository.updateIdeaStatus("id1", IdeaStatus.APROVADA)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ideias/id1/aprovar", recordedRequest.path)
        assertEquals("PATCH", recordedRequest.method)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `arquivar handles 204 No Content response successfully`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val result = repository.arquivar("id1")

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ideias/id1", recordedRequest.path)
        assertEquals("DELETE", recordedRequest.method)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `createIdea handles HTTP 400 error`() = runTest {
        val errorJson = """{"status":400,"erro":"Bad Request","mensagem":"A categoria é obrigatória"}"""
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody(errorJson))

        val result = repository.createIdea("T", "P", "S", "B", "", "est1")

        assertTrue(result.isFailure)
        assertEquals("A categoria é obrigatória", result.exceptionOrNull()?.message)
    }
}
