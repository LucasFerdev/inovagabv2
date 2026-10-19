package br.com.inovagabv2.presentation.leadership

import br.com.inovagabv2.domain.model.Role
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StrategyDashboardEditVisibilityTest {

    @Test
    fun `editar estrategia fica visivel apenas para lideranca`() {
        assertTrue(canEditStrategy(Role.LIDERANCA))
        assertFalse(canEditStrategy(Role.GESTOR))
        assertFalse(canEditStrategy(Role.OPERADOR))
        assertFalse(canEditStrategy(null))
    }
}
