package br.com.inovagabv2.presentation.auth

import br.com.inovagabv2.domain.model.User

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val createdUser: User? = null
)
