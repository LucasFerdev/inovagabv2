package br.com.inovagabv2.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyFormatterTest {

    @Test
    fun `formatCurrency uses brazilian grouping and two decimal places`() {
        assertEquals("R$ 1.500,00", formatCurrency(BigDecimal("1500")))
        assertEquals("R$ 999,99", formatCurrency(BigDecimal("999.99")))
        assertEquals("R$ 1.000,00", formatCurrency(BigDecimal("1000")))
        assertEquals("R$ 250.000,00", formatCurrency(BigDecimal("250000")))
        assertEquals("R$ 1.250.000,00", formatCurrency(BigDecimal("1250000")))
        assertEquals("R$ 0,00", formatCurrency(BigDecimal.ZERO))
        assertEquals("R$ 0,00", formatCurrency(null))
        assertEquals("-R$ 1.500,00", formatCurrency(BigDecimal("-1500")))
    }

    @Test
    fun `formatCurrencyCompact abbreviates thousands and millions`() {
        assertEquals("R$ 1.500,00", formatCurrency(BigDecimal("1500")))
        assertEquals("R$ 1,5 mil", formatCurrencyCompact(BigDecimal("1500")))
        assertEquals("R$ 999,99", formatCurrencyCompact(BigDecimal("999.99")))
        assertEquals("R$ 1 mil", formatCurrencyCompact(BigDecimal("1000")))
        assertEquals("R$ 250 mil", formatCurrencyCompact(BigDecimal("250000")))
        assertEquals("R$ 1,25 mi", formatCurrencyCompact(BigDecimal("1250000")))
        assertEquals("R$ 0,00", formatCurrencyCompact(BigDecimal.ZERO))
        assertEquals("R$ 0,00", formatCurrencyCompact(null))
        assertEquals("-R$ 1,5 mil", formatCurrencyCompact(BigDecimal("-1500")))
    }

    @Test
    fun `formatCurrencyCompact promotes the band after rounding`() {
        assertEquals("R$ 1 mi", formatCurrencyCompact(BigDecimal("999960")))
        assertEquals("R$ 1 mil", formatCurrencyCompact(BigDecimal("999.996")))
    }

    @Test
    fun `formatPercent uses comma as decimal separator`() {
        assertEquals("12,5%", formatPercent(BigDecimal("12.5")))
        assertEquals("46,7%", formatPercent(BigDecimal("46.7")))
    }
}
