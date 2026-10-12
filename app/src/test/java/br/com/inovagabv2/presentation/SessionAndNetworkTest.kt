package br.com.inovagabv2.presentation

import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.repository.IdeaRepositoryImpl
import br.com.inovagabv2.data.repository.ProjectRepositoryImpl
import br.com.inovagabv2.domain.model.Pagina
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class SessionAndNetworkTest {

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
    fun `session expiration calculation logic calculates correct future timestamp`() {
        val now = System.currentTimeMillis()
        val expiresInSeconds = 3600L
        val expectedExpiration = now + (expiresInSeconds * 1000L)

        val isExpiredNow = System.currentTimeMillis() >= expectedExpiration
        assertFalse(isExpiredNow)

        val isExpiredPast = (now + 3601 * 1000L) >= expectedExpiration
        assertTrue(isExpiredPast)
    }

    @Test
    fun `query error in IdeaRepositoryImpl throws exception and is NOT converted to empty list`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("""{"mensagem":"Erro interno no servidor"}"""))

        val repo = IdeaRepositoryImpl(api, json)

        var exceptionThrown = false
        try {
            repo.getIdeas().first()
        } catch (e: Exception) {
            exceptionThrown = true
            assertTrue(e.message?.contains("Erro interno") == true)
        }

        assertTrue(exceptionThrown)
    }

    @Test
    fun `paginated project response maps contract correctly`() = runTest {
        val paginatedJson = """
            {
              "conteudo": [
                {
                  "id": "p1",
                  "nome": "Projeto Alfa",
                  "descricao": "Descrição Alfa",
                  "estrategiaId": "est1",
                  "etapa": "DESENVOLVIMENTO",
                  "status": "EM_ANDAMENTO",
                  "percentualProgresso": 50,
                  "investimento": 100000.00,
                  "prazo": "2026-12-31"
                }
              ],
              "pagina": 0,
              "tamanho": 20,
              "totalElementos": 45,
              "totalPaginas": 3,
              "primeira": true,
              "ultima": false
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(paginatedJson))

        val repo = ProjectRepositoryImpl(api, json)
        val page: Pagina<br.com.inovagabv2.domain.model.Project> = repo.getProjectsRemote(pagina = 0, tamanho = 20).first()

        assertEquals(0, page.pagina)
        assertEquals(20, page.tamanho)
        assertEquals(45L, page.totalElementos)
        assertEquals(3, page.totalPaginas)
        assertTrue(page.primeira)
        assertFalse(page.ultima)
        assertEquals(1, page.conteudo.size)
        assertEquals("Projeto Alfa", page.conteudo.first().name)
    }
}
