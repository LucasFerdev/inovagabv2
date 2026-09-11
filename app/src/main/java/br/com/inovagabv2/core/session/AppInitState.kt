package br.com.inovagabv2.core.session

import br.com.inovagabv2.domain.model.User

sealed interface AppInitState {
    data object Loading : AppInitState
    data class Authenticated(val user: User) : AppInitState
    data class Unauthenticated(val onboardingCompleted: Boolean) : AppInitState
}
