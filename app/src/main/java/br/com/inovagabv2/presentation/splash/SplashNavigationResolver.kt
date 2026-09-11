package br.com.inovagabv2.presentation.splash

import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.core.session.AppInitState
import br.com.inovagabv2.domain.model.Role

object SplashNavigationResolver {
    fun resolveDestination(state: AppInitState): String? {
        return when (state) {
            AppInitState.Loading -> null
            is AppInitState.Authenticated -> {
                when (state.user.role) {
                    Role.OPERADOR -> Screen.OperatorHome.route
                    Role.GESTOR -> Screen.ManagerHome.route
                    Role.LIDERANCA -> Screen.LeadershipDashboard.route
                }
            }
            is AppInitState.Unauthenticated -> {
                if (state.onboardingCompleted) {
                    Screen.Login.route
                } else {
                    Screen.Onboarding.route
                }
            }
        }
    }
}
