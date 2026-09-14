package br.com.inovagabv2.data.repository

import br.com.inovagabv2.data.remote.api.InovaGabApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.math.BigDecimal

class DashboardRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private lateinit var repository: DashboardRepositoryImpl
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

        repository = DashboardRepositoryImpl(api, json)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getDashboardData fetches summary metrics and maps BigDecimal values`() = runTest {
        val jsonSummary = """
            {
              "totalProjetos": 10,
              "totalPlanejado": 2,
              "totalEmAndamento": 5,
              "totalPausado": 1,
              "totalConcluido": 2,
              "totalCancelado": 0,
              "investimentoTotal": 1500000.00,
              "retornoFinanceiroTotal": 2200000.00,
              "lucroObtido": 700000.00,
              "roiPercentual": 46.7,
              "progressoMedio": 60.0,
              "ganhoMedioProdutividade": 12.5,
              "projetosAtrasados": 1,
              "ideiasEnviadas": 20,
              "ideiasEmAnalise": 3,
              "ideiasAprovadas": 12,
              "ideiasRejeitadas": 5,
              "projetosPorStatus": {"EM_ANDAMENTO": 5, "PLANEJADO": 2},
              "ideiasPorStatus": {"APROVADA": 12, "EM_ANALISE": 3}
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonSummary))

        val data = repository.getDashboardData().first()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/dashboard/resumo", recordedRequest.path)

        assertEquals(10L, data.totalProjetos)
        assertEquals(BigDecimal("700000.00"), data.lucroObtido)
        assertEquals(BigDecimal("46.7"), data.roiPercentual)
        assertEquals("46,7%", data.roi)
        assertEquals(1, data.delayedProjectsCount)
    }

    @Test
    fun `getDashboardStrategyDetails fetches strategy indicators`() = runTest {
        val jsonStrategy = """
            {
              "estrategiaId": "est1",
              "titulo": "Eficiência Operacional",
              "status": "ATIVA",
              "quantidadeIdeias": 8,
              "ideiasAprovadas": 5,
              "quantidadeProjetos": 3,
              "investimento": 500000.00,
              "retorno": 850000.00,
              "lucro": 350000.00,
              "roiPercentual": 70.0,
              "progressoMedio": 75.0,
              "produtividadeMedia": 15.0,
              "projetosAtrasados": 0
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonStrategy))

        val details = repository.getDashboardStrategyDetails("est1").first()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/dashboard/estrategias/est1", recordedRequest.path)

        assertNotNull(details)
        assertEquals("Eficiência Operacional", details?.titulo)
        assertEquals(BigDecimal("70.0"), details?.roiPercentual)
    }
}
