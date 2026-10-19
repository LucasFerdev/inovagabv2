package br.com.inovagabv2.core.util

import java.math.BigDecimal

private val THOUSANDS_PATTERN = Regex("^\\d{1,3}(\\.\\d{3})+$")

fun parseDecimalBr(input: String): BigDecimal? {
    val cleaned = input.replace(" ", "").replace("R$", "")
    if (cleaned.isEmpty()) return null

    val normalized = when {
        ',' in cleaned -> cleaned.replace(".", "").replace(',', '.')
        THOUSANDS_PATTERN.matches(cleaned) -> cleaned.replace(".", "")
        else -> cleaned
    }

    val value = try {
        BigDecimal(normalized)
    } catch (_: NumberFormatException) {
        return null
    }

    if (value.signum() < 0) return null
    return value
}

fun formatDecimalBr(value: BigDecimal): String =
    value.stripTrailingZeros().toPlainString().replace('.', ',')
