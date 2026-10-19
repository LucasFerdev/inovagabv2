package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.core.di.NetworkModule
import br.com.inovagabv2.data.remote.dto.project.ProjetoResponseDto
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectMapperTest {

    private val json = NetworkModule.provideJson()

    @Test
    fun `decode and map atrasado true`() {
        val decoded = json.decodeFromString<ProjetoResponseDto>(projetoJson(atrasado = "true"))
        val project = decoded.toDomain()

        assertTrue(decoded.atrasado)
        assertTrue(project.isAtrasado)
    }

    @Test
    fun `decode and map atrasado false`() {
        val decoded = json.decodeFromString<ProjetoResponseDto>(projetoJson(atrasado = "false"))
        val project = decoded.toDomain()

        assertFalse(decoded.atrasado)
        assertFalse(project.isAtrasado)
    }

    @Test
    fun `missing atrasado defaults to false`() {
        val decoded = json.decodeFromString<ProjetoResponseDto>(projetoJson(atrasado = null))
        val project = decoded.toDomain()

        assertFalse(decoded.atrasado)
        assertFalse(project.isAtrasado)
        assertEquals("p1", project.id)
    }

    private fun projetoJson(atrasado: String?): String {
        val atrasadoField = if (atrasado == null) "" else ""","atrasado": $atrasado"""
        return """
            {
              "id": "p1",
              "nome": "Embarque inteligente",
              "descricao": "Desc",
              "estrategiaId": "est1",
              "etapa": "IMPLEMENTACAO",
              "status": "EM_ANDAMENTO",
              "percentualProgresso": 65,
              "investimento": 420000.00,
              "prazo": "2026-10-30"$atrasadoField
            }
        """.trimIndent()
    }
}
