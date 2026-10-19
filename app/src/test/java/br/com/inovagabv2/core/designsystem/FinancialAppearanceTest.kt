package br.com.inovagabv2.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class FinancialAppearanceTest {

    @Test
    fun `valor positivo usa verde e seta para cima`() {
        val light = financialAppearance(BigDecimal("12.5"))
        assertEquals(FinancialSignal.POSITIVE, light.signal)
        assertEquals(Color(0xFF16A34A), light.contentColor)
        assertEquals(Icons.Outlined.TrendingUp, light.icon)

        val dark = financialAppearance(BigDecimal("1"), onDark = true)
        assertEquals(FinancialSignal.POSITIVE, dark.signal)
        assertEquals(Color(0xFF34D399), dark.contentColor)
        assertEquals(Color(0xFF064E3B), dark.containerColor)
        assertEquals(Icons.Outlined.TrendingUp, dark.icon)
    }

    @Test
    fun `valor negativo usa vermelho e seta para baixo`() {
        val appearance = financialAppearance(BigDecimal("-20.0"))
        assertEquals(FinancialSignal.NEGATIVE, appearance.signal)
        assertEquals(AguiaColors.ErrorRed, appearance.contentColor)
        assertEquals(Icons.Outlined.TrendingDown, appearance.icon)

        val dark = financialAppearance(BigDecimal("-8"), onDark = true)
        assertEquals(FinancialSignal.NEGATIVE, dark.signal)
        assertEquals(AguiaColors.ErrorRed, dark.contentColor)
        assertEquals(Icons.Outlined.TrendingDown, dark.icon)
    }

    @Test
    fun `valor zero usa cor neutra sem seta`() {
        val appearance = financialAppearance(BigDecimal.ZERO)
        assertEquals(FinancialSignal.NEUTRAL, appearance.signal)
        assertEquals(AguiaColors.TextSecondary, appearance.contentColor)
        assertNull(appearance.icon)

        val dark = financialAppearance(BigDecimal("0.00"), onDark = true)
        assertEquals(FinancialSignal.NEUTRAL, dark.signal)
        assertNull(dark.icon)
    }

    @Test
    fun `texto formatado delega o sinal para a mesma funcao`() {
        assertEquals(FinancialSignal.NEGATIVE, financialAppearanceOfDisplay("ROI -20,0%").signal)
        assertEquals(FinancialSignal.NEGATIVE, financialAppearanceOfDisplay("-R$ 8.000,00").signal)
        assertEquals(FinancialSignal.NEUTRAL, financialAppearanceOfDisplay("0,0%").signal)
        assertEquals(FinancialSignal.POSITIVE, financialAppearanceOfDisplay("12,5%").signal)
        assertEquals(Icons.Outlined.TrendingDown, financialAppearanceOfDisplay("-20,0%").icon)
        assertNull(financialAppearanceOfDisplay("R$ 0,00").icon)
    }
}
