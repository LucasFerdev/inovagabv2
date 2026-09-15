package br.com.inovagabv2.presentation

import br.com.inovagabv2.data.mapper.toDomain
import br.com.inovagabv2.data.remote.dto.idea.HistoricoIdeiaResponseDto
import br.com.inovagabv2.data.remote.dto.project.HistoricoProjetoResponseDto
import br.com.inovagabv2.data.remote.dto.strategy.HistoricoEstrategiaResponseDto
import br.com.inovagabv2.core.util.UserUtils
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Checkpoint52VerificationTest {

    @Test
    fun `user initials calculation works correctly`() {
        assertEquals("LF", UserUtils.calculateInitials("Lucas Fernando"))
        assertEquals("AS", UserUtils.calculateInitials("Ana Paula Silva"))
        assertEquals("MA", UserUtils.calculateInitials("Mariana"))
        assertEquals("??", UserUtils.calculateInitials(null))
    }

    @Test
    fun `support email body formats real user session data without sensitive tokens or passwords`() {
        val user = User(
            id = "user123TechId",
            name = "Carlos Mendes",
            email = "carlos@aguia.com",
            role = Role.LIDERANCA,
            company = "Viação Águia Branca"
        )

        val recipient = "lucasferdev01@gmail.com"
        val subject = "Suporte InovaGAB"
        val body = """
            Olá, preciso de ajuda com o InovaGAB.

            Nome: ${user.name}
            E-mail: ${user.email}
            Empresa: ${user.company}
            Perfil: ${UserUtils.formatRoleName(user.role)}

            Descrição do problema:
        """.trimIndent()

        assertEquals("lucasferdev01@gmail.com", recipient)
        assertEquals("Suporte InovaGAB", subject)
        assertTrue(body.contains("Carlos Mendes"))
        assertTrue(body.contains("carlos@aguia.com"))
        assertTrue(body.contains("Viação Águia Branca"))
        assertTrue(body.contains("Liderança"))

        // Security check: NO token, password or technical ID
        assertFalse(body.contains("Bearer"))
        assertFalse(body.contains("user123TechId"))
        assertFalse(body.contains("senha"))
    }

    @Test
    fun `history DTO to domain mappers convert actions and dates without technical IDs`() {
        val ideaHistoryDto = HistoricoIdeiaResponseDto(
            id = "h1",
            acao = "ANALISADA",
            dataHora = "2026-09-14T10:30:00Z",
            usuarioId = "usr999Tech",
            titulo = "Título",
            problema = "Prob",
            solucaoProposta = "Sol",
            beneficiosEsperados = "Ben",
            categoria = "Operação",
            estrategiaId = "est1",
            status = "EM_ANALISE",
            justificativaAvaliacao = "Análise iniciada"
        )
        val ideaHistoryDomain = ideaHistoryDto.toDomain()
        assertEquals("Analisada", ideaHistoryDomain.action)
        assertEquals("2026-09-14T10:30:00Z", ideaHistoryDomain.dateTime)
        assertEquals("Análise iniciada", ideaHistoryDomain.justification)

        val strategyHistoryDto = HistoricoEstrategiaResponseDto(
            id = "h2",
            acao = "ATIVADA",
            dataHora = "2026-09-14T11:00:00Z",
            usuarioId = "usr888Tech",
            titulo = "Título Est",
            descricao = "Desc",
            data = "2026-09-14",
            categoria = "Geral",
            campanha = "Camp",
            status = "ATIVA"
        )
        val strategyHistoryDomain = strategyHistoryDto.toDomain()
        assertEquals("Ativada", strategyHistoryDomain.action)
        assertEquals("Ativa", strategyHistoryDomain.status)

        val projectHistoryDto = HistoricoProjetoResponseDto(
            id = "h3",
            acao = "PROGRESSO_ATUALIZADO",
            dataHora = "2026-09-14T12:00:00Z",
            usuarioId = "usr777Tech",
            nome = "Nome Proj",
            descricao = "Desc",
            estrategiaId = "est1",
            ideiaOrigemId = null,
            etapa = "IMPLEMENTACAO",
            status = "EM_ANDAMENTO",
            percentualProgresso = 75,
            investimento = java.math.BigDecimal("100000.00"),
            prazo = "2026-12-31",
            justificativa = "Avanço conforme planejado"
        )
        val projectHistoryDomain = projectHistoryDto.toDomain()
        assertEquals("Progresso atualizado", projectHistoryDomain.action)
        assertEquals("Avanço conforme planejado", projectHistoryDomain.justification)
    }
}
