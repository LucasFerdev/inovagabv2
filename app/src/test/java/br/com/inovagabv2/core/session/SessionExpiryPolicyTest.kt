package br.com.inovagabv2.core.session

import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionExpiryPolicyTest {

    private val user = User("1", "Usuário", "user@aguia.com", Role.OPERADOR)
    private val authenticated = AppInitState.Authenticated(user)
    private val unauthenticated = AppInitState.Unauthenticated(onboardingCompleted = true)

    @Test
    fun `401 com usuario logado emite evento de sessao expirada uma vez`() {
        val policy = SessionExpiryPolicy()

        assertNull(policy.onAppInitState(authenticated, Screen.OperatorHome.route))
        assertEquals(
            SessionExpiryPolicy.MESSAGE,
            policy.onAppInitState(unauthenticated, Screen.OperatorHome.route),
        )
        assertNull(policy.onAppInitState(unauthenticated, Screen.OperatorHome.route))
    }

    @Test
    fun `logout manual nao emite evento de expiracao`() {
        val policy = SessionExpiryPolicy()

        assertNull(policy.onAppInitState(authenticated, Screen.Profile.route))
        assertNull(
            policy.onAppInitState(
                state = unauthenticated,
                currentRoute = Screen.Profile.route,
                manualLogoutMark = true,
            ),
        )
    }

    @Test
    fun `varios 401 seguidos emitem um unico evento`() {
        val policy = SessionExpiryPolicy()
        policy.onAppInitState(authenticated, Screen.ManagerHome.route)

        val events = List(3) {
            policy.onAppInitState(unauthenticated, Screen.ManagerHome.route)
        }

        assertEquals(listOf(SessionExpiryPolicy.MESSAGE, null, null), events)
    }

    @Test
    fun `logout manual depois login e 401 emite o evento`() {
        val policy = SessionExpiryPolicy()

        assertNull(policy.onAppInitState(authenticated, Screen.Profile.route))
        assertNull(
            policy.onAppInitState(
                state = unauthenticated,
                currentRoute = Screen.Profile.route,
                manualLogoutMark = true,
            ),
        )
        assertNull(policy.onAppInitState(authenticated, Screen.OperatorHome.route))
        assertEquals(
            SessionExpiryPolicy.MESSAGE,
            policy.onAppInitState(unauthenticated, Screen.OperatorHome.route),
        )
    }

    @Test
    fun `splash onboarding login e cadastro nao emitem evento`() {
        val routes = listOf(
            Screen.Splash.route,
            Screen.Onboarding.route,
            Screen.Login.route,
            Screen.Register.route,
        )

        routes.forEach { route ->
            val policy = SessionExpiryPolicy()
            policy.onAppInitState(authenticated, route)
            assertNull(policy.onAppInitState(unauthenticated, route))
        }
    }
}
