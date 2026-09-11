package br.com.inovagabv2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val timestamp: String? = null,
    val status: Int? = null,
    val erro: String? = null,
    val mensagem: String? = null,
    val path: String? = null
)
