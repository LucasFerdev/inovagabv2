package br.com.inovagabv2.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class MoneyParserTest {

    @Test
    fun `parseDecimalBr accepts brazilian and plain decimals`() {
        assertEquals(BigDecimal("1500.00"), parseDecimalBr("1.500,00"))
        assertEquals(BigDecimal("1500.5"), parseDecimalBr("1500,5"))
        assertEquals(BigDecimal("1500.50"), parseDecimalBr("1500.50"))
        assertEquals(BigDecimal("1500"), parseDecimalBr("1.500"))
        assertEquals(BigDecimal("2000.00"), parseDecimalBr("R$ 2.000,00"))
        assertEquals(BigDecimal("12.5"), parseDecimalBr("12,5"))
    }

    @Test
    fun `parseDecimalBr rejects invalid and negative input`() {
        assertNull(parseDecimalBr("abc"))
        assertNull(parseDecimalBr("-10"))
        assertNull(parseDecimalBr(""))
    }

    @Test
    fun `format then parseDecimalBr preserves the same value`() {
        listOf(
            "12.500" to "12,5",
            "2500.75" to "2500,75",
            "1500.00" to "1500",
            "100.000" to "100"
        ).forEach { (raw, expectedText) ->
            val original = BigDecimal(raw)
            val formatted = formatDecimalBr(original)
            assertEquals(expectedText, formatted)
            assertEquals(0, original.compareTo(checkNotNull(parseDecimalBr(formatted))))
        }
    }
}
