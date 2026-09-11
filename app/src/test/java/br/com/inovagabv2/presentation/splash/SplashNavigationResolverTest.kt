package br.com.inovagabv2.presentation.splash

import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.core.session.AppInitState
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SplashNavigationResolverTest {

    @Test
    fun `Loading state does not direct prematurely to onboarding`() {
        val destination = SplashNavigationResolver.resolveDestination(AppInitState.Loading)
        assertNull(destination)
    }

    @Test
    fun `first opening without session directs to onboarding`() {
        val state = AppInitState.Unauthenticated(onboardingCompleted = false)
        val destination = SplashNavigationResolver.resolveDestination(state)
        assertEquals(Screen.Onboarding.route, destination)
    }

    @Test
    fun `subsequent opening without session directs to login`() {
        val state = AppInitState.Unauthenticated(onboardingCompleted = true)
        val destination = SplashNavigationResolver.resolveDestination(state)
        assertEquals(Screen.Login.route, destination)
    }

    @Test
    fun `OPERADOR session directs to operator screen`() {
        val user = User("1", "Operador", "op@aguia.com", Role.OPERADOR)
        val state = AppInitState.Authenticated(user)
        val destination = SplashNavigationResolver.resolveDestination(state)
        assertEquals(Screen.OperatorHome.route, destination)
    }

    @Test
    fun `GESTOR session directs to manager screen`() {
        val user = User("2", "Gestor", "gestor@aguia.com", Role.GESTOR)
        val state = AppInitState.Authenticated(user)
        val destination = SplashNavigationResolver.resolveDestination(state)
        assertEquals(Screen.ManagerHome.route, destination)
    }

    @Test
    fun `LIDERANCA session directs to leadership dashboard`() {
        val user = User("3", "Lider", "lider@aguia.com", Role.LIDERANCA)
        val state = AppInitState.Authenticated(user)
        val destination = SplashNavigationResolver.resolveDestination(state)
        assertEquals(Screen.LeadershipDashboard.route, destination)
    }
}
