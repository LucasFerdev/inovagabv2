package br.com.inovagabv2.core.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val BRAZIL = Locale.forLanguageTag("pt-BR")
private val THOUSAND = BigDecimal("1000")

fun formatCurrency(value: BigDecimal?): String {
    val amount = value ?: BigDecimal.ZERO
    val formatted = currencyFormat().format(amount.abs())
    return if (amount.signum() < 0) "-$formatted" else formatted
}

fun formatCurrencyCompact(value: BigDecimal?): String {
    val amount = value ?: BigDecimal.ZERO
    val negative = amount.signum() < 0
    val rounded = amount.abs().setScale(2, RoundingMode.HALF_UP)
    val formatted = when {
        rounded < THOUSAND -> currencyFormat().format(rounded)
        else -> {
            val thousands = rounded.divide(THOUSAND, 1, RoundingMode.HALF_UP)
            if (thousands < THOUSAND) {
                "R$ ${compactNumber(thousands)} mil"
            } else {
                val millions = thousands.divide(THOUSAND, 2, RoundingMode.HALF_UP)
                "R$ ${compactNumber(millions)} mi"
            }
        }
    }
    return if (negative) "-$formatted" else formatted
}

fun formatPercent(value: BigDecimal?): String {
    val symbols = DecimalFormatSymbols.getInstance(BRAZIL)
    val format = DecimalFormat("0.0", symbols).apply {
        roundingMode = RoundingMode.HALF_UP
    }
    return format.format(value ?: BigDecimal.ZERO) + "%"
}

private fun currencyFormat(): DecimalFormat {
    val symbols = DecimalFormatSymbols.getInstance(BRAZIL)
    return DecimalFormat("¤ #,##0.00", symbols).apply {
        roundingMode = RoundingMode.HALF_UP
    }
}

private fun compactNumber(value: BigDecimal): String =
    value.stripTrailingZeros().toPlainString().replace('.', ',')
