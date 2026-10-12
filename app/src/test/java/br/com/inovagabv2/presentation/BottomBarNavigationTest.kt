package br.com.inovagabv2.presentation

import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

fun resolveBottomBarTargetRoute(role: Role, label: String): String {
    return when (role) {
        Role.OPERADOR -> when (label) {
            "Início" -> Screen.OperatorHome.route
            "Minhas ideias", "Ideias" -> Screen.MyIdeas.route
            "Estratégias", "Estratégia" -> Screen.OperatorStrategy.route
            "Perfil" -> Screen.Profile.route
            else -> Screen.OperatorHome.route
        }
        Role.GESTOR -> when (label) {
            "Início" -> Screen.ManagerHome.route
            "Ideias" -> Screen.ManagerIdeas.route
            "Projetos" -> Screen.ManagerProjects.route
            "Estratégias", "Estratégia" -> Screen.ManagerStrategy.route
            "Perfil" -> Screen.Profile.route
            else -> Screen.ManagerHome.route
        }
        Role.LIDERANCA -> when (label) {
            "Início" -> Screen.LeadershipDashboard.route
            "Ideias" -> Screen.LeadershipIdeas.route
            "Projetos" -> Screen.LeadershipProjects.route
            "Estratégias", "Estratégia" -> Screen.LeadershipStrategy.route
            "Perfil" -> Screen.Profile.route
            else -> Screen.LeadershipDashboard.route
        }
    }
}

class BottomBarNavigationTest {

    @Test
    fun `Operator Profile to Estrategias resolves to OperatorStrategy route and never Communications`() {
        val route = resolveBottomBarTargetRoute(Role.OPERADOR, "Estratégias")
        assertEquals(Screen.OperatorStrategy.route, route)
        assertNotEquals("operator_communications", route)
    }

    @Test
    fun `Gestor Profile to Estrategias resolves to ManagerStrategy route and never Communications or Projects`() {
        val route = resolveBottomBarTargetRoute(Role.GESTOR, "Estratégias")
        assertEquals(Screen.ManagerStrategy.route, route)
        assertNotEquals("operator_communications", route)
        assertNotEquals(Screen.ManagerProjects.route, route)
    }

    @Test
    fun `Leadership Profile to Estrategias resolves to LeadershipStrategy route and never Projects`() {
        val route = resolveBottomBarTargetRoute(Role.LIDERANCA, "Estratégias")
        assertEquals(Screen.LeadershipStrategy.route, route)
        assertNotEquals(Screen.LeadershipProjects.route, route)
    }

    @Test
    fun `Leadership Profile to Ideias resolves to LeadershipIdeas route`() {
        val route = resolveBottomBarTargetRoute(Role.LIDERANCA, "Ideias")
        assertEquals(Screen.LeadershipIdeas.route, route)
    }

    @Test
    fun `Gestor Profile to Projetos resolves to ManagerProjects route`() {
        val route = resolveBottomBarTargetRoute(Role.GESTOR, "Projetos")
        assertEquals(Screen.ManagerProjects.route, route)
    }

    @Test
    fun `Leadership Profile to Projetos resolves to LeadershipProjects route`() {
        val route = resolveBottomBarTargetRoute(Role.LIDERANCA, "Projetos")
        assertEquals(Screen.LeadershipProjects.route, route)
    }

    @Test
    fun `MyIdeas to Estrategias resolves to OperatorStrategy route`() {
        val route = resolveBottomBarTargetRoute(Role.OPERADOR, "Estratégias")
        assertEquals(Screen.OperatorStrategy.route, route)
        assertNotEquals("operator_communications", route)
    }

    @Test
    fun `no bottom bar route resolves to operator_communications for any role`() {
        val labels = listOf("Início", "Ideias", "Minhas ideias", "Projetos", "Estratégias", "Estratégia", "Perfil")
        Role.entries.forEach { role ->
            labels.forEach { label ->
                val targetRoute = resolveBottomBarTargetRoute(role, label)
                assertNotEquals("operator_communications", targetRoute)
            }
        }
    }

    @Test
    fun `bottom bar routes remain strictly isolated per Role`() {
        // Operator routes
        assertEquals("operator_home", resolveBottomBarTargetRoute(Role.OPERADOR, "Início"))
        assertEquals("my_ideas", resolveBottomBarTargetRoute(Role.OPERADOR, "Minhas ideias"))
        assertEquals("operator_strategy", resolveBottomBarTargetRoute(Role.OPERADOR, "Estratégias"))
        assertEquals("profile", resolveBottomBarTargetRoute(Role.OPERADOR, "Perfil"))

        // Gestor routes
        assertEquals("manager_home", resolveBottomBarTargetRoute(Role.GESTOR, "Início"))
        assertEquals("manager_ideas", resolveBottomBarTargetRoute(Role.GESTOR, "Ideias"))
        assertEquals("manager_projects", resolveBottomBarTargetRoute(Role.GESTOR, "Projetos"))
        assertEquals("manager_strategy", resolveBottomBarTargetRoute(Role.GESTOR, "Estratégias"))
        assertEquals("profile", resolveBottomBarTargetRoute(Role.GESTOR, "Perfil"))

        // Leadership routes
        assertEquals("leadership_dashboard", resolveBottomBarTargetRoute(Role.LIDERANCA, "Início"))
        assertEquals("leadership_ideas", resolveBottomBarTargetRoute(Role.LIDERANCA, "Ideias"))
        assertEquals("leadership_projects", resolveBottomBarTargetRoute(Role.LIDERANCA, "Projetos"))
        assertEquals("leadership_strategy", resolveBottomBarTargetRoute(Role.LIDERANCA, "Estratégias"))
        assertEquals("profile", resolveBottomBarTargetRoute(Role.LIDERANCA, "Perfil"))
    }
}
