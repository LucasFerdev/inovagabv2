package br.com.inovagabv2.data.remote.dto

import br.com.inovagabv2.domain.model.Role
import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioResponseDtoTest {

    @Test
    fun `toDomain should map usuario DTO to User domain correctly for OPERADOR`() {
        val dto = UsuarioResponseDto(
            id = "user123",
            nome = "João Silva",
            email = "joao@aguia.com",
            empresa = "Águia Branca",
            role = "OPERADOR",
            ativo = true,
            criadoEm = "2026-01-01T00:00:00Z",
            atualizadoEm = "2026-01-01T00:00:00Z"
        )

        val domainUser = dto.toDomain()

        assertEquals("user123", domainUser.id)
        assertEquals("João Silva", domainUser.name)
        assertEquals("joao@aguia.com", domainUser.email)
        assertEquals(Role.OPERADOR, domainUser.role)
    }

    @Test
    fun `toDomain should map usuario DTO to User domain correctly for GESTOR`() {
        val dto = UsuarioResponseDto(
            id = "user456",
            nome = "Mariana Costa",
            email = "mariana@aguia.com",
            empresa = "Águia Branca",
            role = "GESTOR"
        )

        val domainUser = dto.toDomain()

        assertEquals(Role.GESTOR, domainUser.role)
    }

    @Test
    fun `toDomain should map usuario DTO to User domain correctly for LIDERANCA`() {
        val dto = UsuarioResponseDto(
            id = "user789",
            nome = "Carlos Mendes",
            email = "carlos@aguia.com",
            empresa = "Águia Branca",
            role = "LIDERANCA"
        )

        val domainUser = dto.toDomain()

        assertEquals(Role.LIDERANCA, domainUser.role)
    }

    @Test
    fun `toDomain should fallback to OPERADOR if role string is unknown`() {
        val dto = UsuarioResponseDto(
            id = "user999",
            nome = "Usuário Desconhecido",
            email = "unknown@aguia.com",
            role = "INVALID_ROLE"
        )

        val domainUser = dto.toDomain()

        assertEquals(Role.OPERADOR, domainUser.role)
    }
}
