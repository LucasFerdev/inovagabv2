package br.com.inovagabv2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val token: String,
    val tipo: String? = "Bearer",
    val expiresIn: Long? = null,
    val usuario: UsuarioResponseDto
)
