package br.com.inovagabv2.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val nome: String,
    val email: String,
    val senha: String,
    val empresa: String,
    @SerialName("codigoAcesso")
    val codigoAcesso: String? = null
)
