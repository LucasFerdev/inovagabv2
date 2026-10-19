package br.com.inovagabv2.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.math.BigDecimal

enum class FinancialSignal {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}

data class FinancialAppearance(
    val signal: FinancialSignal,
    val contentColor: Color,
    val containerColor: Color,
    val icon: ImageVector?
)

private val LightPositiveContent = Color(0xFF16A34A)
private val LightPositiveContainer = Color(0xFFDCFCE7)
private val DarkPositiveContent = Color(0xFF34D399)
private val DarkPositiveContainer = Color(0xFF064E3B)

fun financialAppearance(value: BigDecimal, onDark: Boolean = false): FinancialAppearance {
    val signal = when (value.signum()) {
        1 -> FinancialSignal.POSITIVE
        -1 -> FinancialSignal.NEGATIVE
        else -> FinancialSignal.NEUTRAL
    }
    return appearanceFor(signal, onDark)
}

fun financialAppearanceOfDisplay(text: String, onDark: Boolean = false): FinancialAppearance {
    val trimmed = text.trim()
    val negative = trimmed.contains('-') || trimmed.contains('−')
    val digits = trimmed.filter { it.isDigit() }
    val zero = digits.isEmpty() || digits.all { it == '0' }
    val probe = when {
        zero -> BigDecimal.ZERO
        negative -> BigDecimal.ONE.negate()
        else -> BigDecimal.ONE
    }
    return financialAppearance(probe, onDark)
}

private fun appearanceFor(signal: FinancialSignal, onDark: Boolean): FinancialAppearance {
    return when (signal) {
        FinancialSignal.POSITIVE -> FinancialAppearance(
            signal = signal,
            contentColor = if (onDark) DarkPositiveContent else LightPositiveContent,
            containerColor = if (onDark) DarkPositiveContainer else LightPositiveContainer,
            icon = Icons.Outlined.TrendingUp
        )
        FinancialSignal.NEGATIVE -> FinancialAppearance(
            signal = signal,
            contentColor = AguiaColors.ErrorRed,
            containerColor = AguiaColors.ErrorRed.copy(alpha = if (onDark) 0.28f else 0.12f),
            icon = Icons.Outlined.TrendingDown
        )
        FinancialSignal.NEUTRAL -> FinancialAppearance(
            signal = signal,
            contentColor = AguiaColors.TextSecondary,
            containerColor = if (onDark) {
                Color.White.copy(alpha = 0.12f)
            } else {
                AguiaColors.TextSecondary.copy(alpha = 0.12f)
            },
            icon = null
        )
    }
}
