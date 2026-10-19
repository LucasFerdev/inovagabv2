package br.com.inovagabv2.core.util

import br.com.inovagabv2.domain.model.Role
import java.util.Locale

object UserUtils {

    private val nameLocale: Locale = Locale.forLanguageTag("pt-BR")

    fun calculateInitials(name: String?): String {
        if (name.isNullOrBlank()) return ""
        val parts = name.trim().split("\\s+".toRegex()).filter { it.any(Char::isLetter) }
        if (parts.isEmpty()) return ""
        return when (parts.size) {
            1 -> parts[0].filter(Char::isLetter).take(2).uppercase(nameLocale)
            else -> {
                val first = parts.first().first(Char::isLetter)
                val last = parts.last().first(Char::isLetter)
                "$first$last".uppercase(nameLocale)
            }
        }
    }

    fun getFirstName(fullName: String?): String {
        if (fullName.isNullOrBlank()) return "Usuário"
        val first = fullName.trim().split("\\s+".toRegex()).firstOrNull { it.isNotBlank() }
            ?: return "Usuário"
        return formatPersonName(first)
    }

    private fun formatPersonName(token: String): String {
        val letters = token.filter(Char::isLetter)
        val source = if (letters.isNotEmpty() && letters.none(Char::isLowerCase)) {
            token.lowercase(nameLocale)
        } else {
            token
        }
        return source.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(nameLocale) else char.toString()
        }
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
