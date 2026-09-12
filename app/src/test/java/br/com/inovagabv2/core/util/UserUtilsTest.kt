package br.com.inovagabv2.core.util

import br.com.inovagabv2.domain.model.Role
import org.junit.Assert.assertEquals
import org.junit.Test

class UserUtilsTest {

    @Test
    fun `calculateInitials returns correct initials for full names`() {
        assertEquals("LF", UserUtils.calculateInitials("Lucas Fernando"))
        assertEquals("LS", UserUtils.calculateInitials("Lucas Silva"))
        assertEquals("LS", UserUtils.calculateInitials("Lucas Fernando da Silva"))
        assertEquals("LU", UserUtils.calculateInitials("Lucas"))
        assertEquals("??", UserUtils.calculateInitials(null))
        assertEquals("??", UserUtils.calculateInitials(""))
    }

    @Test
    fun `getFirstName extracts first name formatted correctly`() {
        assertEquals("Lucas", UserUtils.getFirstName("lucas fernando da silva"))
        assertEquals("Mariana", UserUtils.getFirstName("MARIANA COSTA"))
        assertEquals("Usuário", UserUtils.getFirstName(null))
    }

    @Test
    fun `formatRoleName translates roles correctly`() {
        assertEquals("Operador", UserUtils.formatRoleName(Role.OPERADOR))
        assertEquals("Gestor", UserUtils.formatRoleName(Role.GESTOR))
        assertEquals("Liderança", UserUtils.formatRoleName(Role.LIDERANCA))
    }

    @Test
    fun `formatRoleBadge formats role badge text in uppercase`() {
        assertEquals("OPERADOR", UserUtils.formatRoleBadge(Role.OPERADOR))
        assertEquals("GESTOR", UserUtils.formatRoleBadge(Role.GESTOR))
        assertEquals("LIDERANÇA", UserUtils.formatRoleBadge(Role.LIDERANCA))
    }
}
