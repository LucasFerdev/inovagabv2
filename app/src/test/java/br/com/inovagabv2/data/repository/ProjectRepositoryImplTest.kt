package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
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
import java.math.BigDecimal

class ProjectRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private lateinit var repository: ProjectRepositoryImpl
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

        repository = ProjectRepositoryImpl(api, json)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `createProject sends POST with numeric investment and returns mapped Project`() = runTest {
        val responseJson = """
            {
              "id": "p100",
              "nome": "Sistema de Frota",
              "descricao": "Monitoramento em tempo real",
              "estrategiaId": "est1",
              "etapa": "DESENVOLVIMENTO",
              "status": "EM_ANDAMENTO",
              "percentualProgresso": 45,
              "investimento": 350000.00,
              "prazo": "2026-11-30",
              "gestorResponsavelId": "g1"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(responseJson))

        val result = repository.createProject(
            nome = "Sistema de Frota",
            descricao = "Monitoramento em tempo real",
            estrategiaId = "est1",
            ideiaOrigemId = null,
            investimento = BigDecimal("350000.00"),
            prazo = "2026-11-30"
        )

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/projetos", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
        assertTrue(recordedRequest.body.readUtf8().contains("350000.00"))

        assertTrue(result.isSuccess)
        val project = result.getOrNull()!!
        assertEquals("p100", project.id)
        assertEquals(ProjectStatus.EM_ANDAMENTO, project.status)
        assertEquals(ProjectStage.DESENVOLVIMENTO, project.stage)
        assertEquals(BigDecimal("350000.00"), project.investment)
    }

    @Test
    fun `getProjectsRemote fetches paginated projects`() = runTest {
        val pageJson = """
            {
              "conteudo": [
                {
                  "id": "p1",
                  "nome": "Projeto 1",
                  "descricao": "Desc 1",
                  "estrategiaId": "est1",
                  "etapa": "PLANEJAMENTO",
                  "status": "PLANEJADO",
                  "percentualProgresso": 10,
                  "investimento": 100000.00,
                  "prazo": "2026-12-31",
                  "gestorResponsavelId": "g1"
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

        val page = repository.getProjectsRemote(pagina = 0, tamanho = 20).first()

        assertEquals(1, page.conteudo.size)
        assertEquals("Projeto 1", page.conteudo.first().name)
        assertEquals(ProjectStatus.PLANEJADO, page.conteudo.first().status)
    }

    @Test
    fun `updateProgress sends PATCH request and returns updated project`() = runTest {
        val responseJson = """
            {
              "id": "p1",
              "nome": "P1",
              "descricao": "D1",
              "estrategiaId": "est1",
              "etapa": "IMPLEMENTACAO",
              "status": "EM_ANDAMENTO",
              "percentualProgresso": 80,
              "investimento": 100000.00,
              "prazo": "2026-12-31",
              "gestorResponsavelId": "g1"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = repository.updateProgress("p1", ProjectStage.IMPLEMENTACAO, ProjectStatus.EM_ANDAMENTO, 80)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/projetos/p1/progresso", recordedRequest.path)
        assertEquals("PATCH", recordedRequest.method)

        assertTrue(result.isSuccess)
        assertEquals(80, result.getOrNull()?.percentualProgresso)
    }

    @Test
    fun `cancelProject handles 204 No Content`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val result = repository.cancelProject("p1")

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/projetos/p1", recordedRequest.path)
        assertEquals("DELETE", recordedRequest.method)

        assertTrue(result.isSuccess)
    }
}
