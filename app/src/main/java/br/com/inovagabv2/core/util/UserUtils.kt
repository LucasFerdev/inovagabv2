package br.com.inovagabv2.core.util

import br.com.inovagabv2.domain.model.Role

object UserUtils {

    fun calculateInitials(name: String?): String {
        if (name.isNullOrBlank()) return "??"
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "??"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts.first().first()}${parts.last().first()}".uppercase()
        }
    }

    fun getFirstName(fullName: String?): String {
        if (fullName.isNullOrBlank()) return "Usuário"
        val first = fullName.trim().split("\\s+".toRegex()).firstOrNull()
        return first?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Usuário"
    }

    fun formatRoleName(role: Role?): String {
        return when (role) {
            Role.OPERADOR -> "Operador"
            Role.GESTOR -> "Gestor"
            Role.LIDERANCA -> "Liderança"
            null -> "Operador"
        }
    }

    fun formatRoleBadge(role: Role?): String {
        return when (role) {
            Role.OPERADOR -> "OPERADOR"
            Role.GESTOR -> "GESTOR"
            Role.LIDERANCA -> "LIDERANÇA"
            null -> "OPERADOR"
        }
    }
}
