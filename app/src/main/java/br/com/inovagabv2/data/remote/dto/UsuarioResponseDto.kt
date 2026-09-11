package br.com.inovagabv2.data.remote.dto

import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioResponseDto(
    val id: String,
    val nome: String,
    val email: String,
    val empresa: String? = null,
    val role: String,
    val ativo: Boolean? = null,
    val criadoEm: String? = null,
    val atualizadoEm: String? = null
)

fun UsuarioResponseDto.toDomain(): User {
    val domainRole = try {
        Role.valueOf(role.uppercase())
    } catch (e: Exception) {
        Role.OPERADOR
    }
    return User(
        id = id,
        name = nome,
        email = email,
        role = domainRole
    )
}
