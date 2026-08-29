package br.com.inovagabv2.presentation.auth

import br.com.inovagabv2.domain.model.User

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successUser: User? = null
)
