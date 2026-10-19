package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.core.di.NetworkModule
import br.com.inovagabv2.data.remote.dto.idea.IdeiaResponseDto
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Test

class IdeaMapperTest {

    private val json = NetworkModule.provideJson()

    @Test
    fun `autorNome preenchido mapeia para authorName`() {
        val decoded = json.decodeFromString<IdeiaResponseDto>(ideiaJson(autorNome = "\"Ana Souza\""))
        val idea = decoded.toDomain()

        assertEquals("Ana Souza", idea.authorName)
    }

    @Test
    fun `json sem autorNome usa fallback`() {
        val decoded = json.decodeFromString<IdeiaResponseDto>(ideiaJson(autorNome = null))
        val idea = decoded.toDomain()

        assertEquals("", idea.authorName)
    }

    @Test
    fun `autorNome null usa fallback`() {
        val decoded = json.decodeFromString<IdeiaResponseDto>(ideiaJson(autorNome = "null"))
        val idea = decoded.toDomain()

        assertEquals("", idea.authorName)
    }

    @Test
    fun `autorNome em branco usa fallback`() {
        val decoded = json.decodeFromString<IdeiaResponseDto>(ideiaJson(autorNome = "\"   \""))
        val idea = decoded.toDomain()

        assertEquals("", idea.authorName)
    }

    private fun ideiaJson(autorNome: String?): String {
        val autorNomeField = if (autorNome == null) "" else ""","autorNome": $autorNome"""
        return """
            {
              "id": "id1",
              "titulo": "Embarque inteligente",
              "problema": "Filas",
              "solucaoProposta": "Sinalização",
              "beneficiosEsperados": "Agilidade",
              "categoria": "Operação",
              "estrategiaId": "est1",
              "autorId": "u1",
              "status": "ENVIADA"$autorNomeField
            }
        """.trimIndent()
    }
}
