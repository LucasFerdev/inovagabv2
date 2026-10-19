package br.com.inovagabv2.core.session

import br.com.inovagabv2.core.navigation.Screen

/**
 * Decide se a perda da sessão deve levar o usuário ao Login com aviso de expiração.
 * Uma única instância deduplica vários 401 e distingue logout manual.
 */
class SessionExpiryPolicy {
    private var wasAuthenticated = false
    private var eventEmitted = false
    private var manualLogout = false

    fun onAppInitState(
        state: AppInitState,
        currentRoute: String?,
        manualLogoutMark: Boolean = false,
    ): String? {
        if (manualLogoutMark) {
            manualLogout = true
        }

        return when (state) {
            AppInitState.Loading -> null
            is AppInitState.Authenticated -> {
                wasAuthenticated = true
                eventEmitted = false
                manualLogout = false
                null
            }
            is AppInitState.Unauthenticated -> onUnauthenticated(currentRoute)
        }
    }

    private fun onUnauthenticated(currentRoute: String?): String? {
        if (!wasAuthenticated || manualLogout || eventEmitted) {
            if (manualLogout) {
                wasAuthenticated = false
            }
            return null
        }
        if (currentRoute == null) {
            return null
        }
        if (currentRoute in AUTH_FLOW_ROUTES) {
            wasAuthenticated = false
            return null
        }
        eventEmitted = true
        wasAuthenticated = false
        return MESSAGE
    }

    companion object {
        const val MESSAGE = "Sua sessão expirou. Entre novamente."

        private val AUTH_FLOW_ROUTES = setOf(
            Screen.Splash.route,
            Screen.Onboarding.route,
            Screen.Login.route,
            Screen.Register.route,
        )
    }
}
