package br.com.inovagabv2.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterRequestDtoTest {

    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    @Test
    fun `JSON should never contain role field`() {
        val dto = RegisterRequestDto(
            nome = "Teste",
            email = "teste@aguia.com",
            senha = "senha123456",
            empresa = "Águia Branca",
            codigoAcesso = "GEST123"
        )

        val jsonString = json.encodeToString(RegisterRequestDto.serializer(), dto)

        assertFalse("JSON must not contain role", jsonString.contains("\"role\""))
        assertTrue("JSON contains codigoAcesso", jsonString.contains("\"codigoAcesso\":\"GEST123\""))
    }

    @Test
    fun `JSON should handle null codigoAcesso gracefully without role`() {
        val dto = RegisterRequestDto(
            nome = "Teste",
            email = "teste@aguia.com",
            senha = "senha123456",
            empresa = "Águia Branca",
            codigoAcesso = null
        )

        val jsonString = json.encodeToString(RegisterRequestDto.serializer(), dto)

        assertFalse("JSON must not contain role", jsonString.contains("\"role\""))
    }
}
