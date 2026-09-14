package br.com.inovagabv2.data.remote.dto

import br.com.inovagabv2.data.remote.dto.ai.AnaliseIaIdeiaResponseDto
import br.com.inovagabv2.data.remote.dto.common.BigDecimalSerializer
import br.com.inovagabv2.data.remote.dto.common.PaginaResponseDto
import br.com.inovagabv2.data.remote.dto.dashboard.DashboardEstrategiaResponseDto
import br.com.inovagabv2.data.remote.dto.dashboard.DashboardResumoResponseDto
import br.com.inovagabv2.data.remote.dto.idea.CriarIdeiaRequestDto
import br.com.inovagabv2.data.remote.dto.idea.IdeiaResponseDto
import br.com.inovagabv2.data.remote.dto.idea.RemoteStatusIdeia
import br.com.inovagabv2.data.remote.dto.project.AtualizarProgressoProjetoRequestDto
import br.com.inovagabv2.data.remote.dto.project.ProjetoResponseDto
import br.com.inovagabv2.data.remote.dto.project.RemoteEtapaProjeto
import br.com.inovagabv2.data.remote.dto.project.RemoteStatusProjeto
import br.com.inovagabv2.data.remote.dto.strategy.CriarEstrategiaRequestDto
import br.com.inovagabv2.data.remote.dto.strategy.EstrategiaResponseDto
import br.com.inovagabv2.data.remote.dto.strategy.RemoteStatusEstrategia
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class ApiSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `test BigDecimalSerializer serialization and deserialization`() {
        @kotlinx.serialization.Serializable
        data class SampleMoney(
            @kotlinx.serialization.Serializable(with = BigDecimalSerializer::class)
            val val1: BigDecimal,
            @kotlinx.serialization.Serializable(with = BigDecimalSerializer::class)
            val val2: BigDecimal
        )

        val jsonInput = """{"val1": 150000.50, "val2": "250000.00"}"""
        val decoded = json.decodeFromString<SampleMoney>(jsonInput)

        assertEquals(BigDecimal("150000.50"), decoded.val1)
        assertEquals(BigDecimal("250000.00"), decoded.val2)

        val encoded = json.encodeToString(SampleMoney.serializer(), decoded)
        assertTrue(encoded.contains("150000.50"))
        assertTrue(encoded.contains("250000.00"))
    }

    @Test
    fun `test PaginaResponseDto deserialization with Portuguese field names`() {
        val jsonInput = """
            {
              "conteudo": [{"id": "1", "titulo": "Ideia 1", "problema": "P1", "solucaoProposta": "S1", "beneficiosEsperados": "B1", "categoria": "Cat1", "estrategiaId": "est1", "autorId": "u1", "status": "ENVIADA"}],
              "pagina": 0,
              "tamanho": 20,
              "totalElementos": 1,
              "totalPaginas": 1,
              "primeira": true,
              "ultima": true
            }
        """.trimIndent()

        val decoded = json.decodeFromString<PaginaResponseDto<IdeiaResponseDto>>(jsonInput)

        assertEquals(0, decoded.pagina)
        assertEquals(20, decoded.tamanho)
        assertEquals(1L, decoded.totalElementos)
        assertEquals(1, decoded.totalPaginas)
        assertTrue(decoded.primeira)
        assertTrue(decoded.ultima)
        assertEquals(1, decoded.conteudo.size)
        assertEquals("Ideia 1", decoded.conteudo.first().titulo)
        assertEquals(RemoteStatusIdeia.ENVIADA, decoded.conteudo.first().status)
    }

    @Test
    fun `test IdeiaResponseDto and CriarIdeiaRequestDto`() {
        val request = CriarIdeiaRequestDto(
            titulo = "Otimização do embarque",
            problema = "Filas nas plataformas",
            solucaoProposta = "Sinalização digital",
            beneficiosEsperados = "Menos tempo de espera",
            categoria = "Operação",
            estrategiaId = "est123"
        )
        val encodedRequest = json.encodeToString(CriarIdeiaRequestDto.serializer(), request)
        assertTrue(encodedRequest.contains("\"titulo\":\"Otimização do embarque\""))

        val responseJson = """
            {
              "id": "id99",
              "titulo": "Otimização do embarque",
              "problema": "Filas",
              "solucaoProposta": "Sinalização",
              "beneficiosEsperados": "Agilidade",
              "categoria": "Operação",
              "estrategiaId": "est123",
              "autorId": "u77",
              "status": "EM_ANALISE",
              "prioridade": null,
              "justificativaAvaliacao": null
            }
        """.trimIndent()
        val decoded = json.decodeFromString<IdeiaResponseDto>(responseJson)

        assertEquals("id99", decoded.id)
        assertEquals(RemoteStatusIdeia.EM_ANALISE, decoded.status)
        assertNull(decoded.prioridade)
    }

    @Test
    fun `test EstrategiaResponseDto and CriarEstrategiaRequestDto`() {
        val request = CriarEstrategiaRequestDto(
            titulo = "Excelência na viagem",
            descricao = "Elevar a qualidade da jornada do cliente",
            data = "2026-09-15",
            categoria = "Experiência do cliente",
            campanha = "Jornada 2026"
        )
        val encodedRequest = json.encodeToString(CriarEstrategiaRequestDto.serializer(), request)
        assertTrue(encodedRequest.contains("\"campanha\":\"Jornada 2026\""))

        val responseJson = """
            {
              "id": "est1",
              "titulo": "Excelência na viagem",
              "descricao": "Desc",
              "data": "2026-09-15",
              "categoria": "Experiência",
              "campanha": "Campanha 1",
              "status": "ATIVA",
              "criadoPorId": "u1",
              "atualizadoPorId": "u1"
            }
        """.trimIndent()
        val decoded = json.decodeFromString<EstrategiaResponseDto>(responseJson)

        assertEquals(RemoteStatusEstrategia.ATIVA, decoded.status)
        assertEquals("2026-09-15", decoded.data)
    }

    @Test
    fun `test ProjetoResponseDto and AtualizarProgressoProjetoRequestDto`() {
        val request = AtualizarProgressoProjetoRequestDto(
            etapa = RemoteEtapaProjeto.DESENVOLVIMENTO,
            status = RemoteStatusProjeto.EM_ANDAMENTO,
            percentualProgresso = 65,
            justificativa = "Avanço conforme cronograma"
        )
        val encodedRequest = json.encodeToString(AtualizarProgressoProjetoRequestDto.serializer(), request)
        assertTrue(encodedRequest.contains("\"DESENVOLVIMENTO\""))

        val responseJson = """
            {
              "id": "p1",
              "nome": "Embarque inteligente",
              "descricao": "Desc",
              "estrategiaId": "est1",
              "etapa": "IMPLEMENTACAO",
              "status": "EM_ANDAMENTO",
              "percentualProgresso": 65,
              "investimento": 420000.00,
              "prazo": "2026-10-30",
              "gestorResponsavelId": "g1"
            }
        """.trimIndent()
        val decoded = json.decodeFromString<ProjetoResponseDto>(responseJson)

        assertEquals(RemoteStatusProjeto.EM_ANDAMENTO, decoded.status)
        assertEquals(RemoteEtapaProjeto.IMPLEMENTACAO, decoded.etapa)
        assertEquals(BigDecimal("420000.00"), decoded.investimento)
    }

    @Test
    fun `test DashboardResumoResponseDto and DashboardEstrategiaResponseDto`() {
        val jsonResumo = """
            {
              "totalProjetos": 12,
              "totalPlanejado": 3,
              "totalEmAndamento": 6,
              "totalPausado": 1,
              "totalConcluido": 2,
              "totalCancelado": 0,
              "investimentoTotal": 3200000.00,
              "retornoFinanceiroTotal": 4680000.00,
              "lucroObtido": 1480000.00,
              "roiPercentual": 46.3,
              "progressoMedio": 64.0,
              "ganhoMedioProdutividade": 15.0,
              "projetosAtrasados": 3,
              "ideiasEnviadas": 27,
              "ideiasEmAnalise": 4,
              "ideiasAprovadas": 18,
              "ideiasRejeitadas": 5,
              "projetosPorStatus": {"EM_ANDAMENTO": 6, "PLANEJADO": 3},
              "ideiasPorStatus": {"APROVADA": 18, "EM_ANALISE": 4}
            }
        """.trimIndent()

        val decoded = json.decodeFromString<DashboardResumoResponseDto>(jsonResumo)

        assertEquals(12L, decoded.totalProjetos)
        assertEquals(BigDecimal("1480000.00"), decoded.lucroObtido)
        assertEquals(BigDecimal("46.3"), decoded.roiPercentual)
        assertEquals(6L, decoded.projetosPorStatus[RemoteStatusProjeto.EM_ANDAMENTO])
        assertEquals(18L, decoded.ideiasPorStatus[RemoteStatusIdeia.APROVADA])

        val jsonEstrategia = """
            {
              "estrategiaId": "est1",
              "titulo": "Eficiência operacional",
              "status": "ATIVA",
              "quantidadeIdeias": 12,
              "ideiasAprovadas": 6,
              "quantidadeProjetos": 4,
              "investimento": 1200000.00,
              "retorno": 1850000.00,
              "lucro": 650000.00,
              "roiPercentual": 54.2,
              "progressoMedio": 65.0,
              "produtividadeMedia": 10.0,
              "projetosAtrasados": 0
            }
        """.trimIndent()

        val decodedEst = json.decodeFromString<DashboardEstrategiaResponseDto>(jsonEstrategia)

        assertEquals("Eficiência operacional", decodedEst.titulo)
        assertEquals(BigDecimal("54.2"), decodedEst.roiPercentual)
    }

    @Test
    fun `test AnaliseIaIdeiaResponseDto`() {
        val jsonIa = """
            {
              "ideiaId": "i10",
              "pontuacaoGeral": 92,
              "prioridadeSugerida": 5,
              "resumoExecutivo": "Proposta de alto impacto",
              "pontosFortes": ["Redução de filas", "Melhoria NPS"],
              "riscos": ["Necessidade de treinamento"],
              "recomendacoes": ["Piloto inicial na rodoviária"],
              "modelo": "gemini-1.5-flash",
              "geradoEm": "2026-09-12T18:00:00Z"
            }
        """.trimIndent()

        val decoded = json.decodeFromString<AnaliseIaIdeiaResponseDto>(jsonIa)

        assertEquals(92, decoded.pontuacaoGeral)
        assertEquals(2, decoded.pontosFortes.size)
        assertEquals("gemini-1.5-flash", decoded.modelo)
    }
}
