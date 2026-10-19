package br.com.inovagabv2.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DateFormatterTest {

    private val zone = ZoneId.of("America/Sao_Paulo")

    @Test
    fun `instant with Z formats date and local time in Sao Paulo`() {
        assertEquals("13/09/2026", formatDate("2026-09-13T15:17:00.146Z", zone))
        assertEquals("13/09/2026 às 12:17", formatDateTime("2026-09-13T15:17:00.146Z", zone))
    }

    @Test
    fun `instant after midnight UTC falls on previous day in Sao Paulo`() {
        assertEquals("13/09/2026", formatDate("2026-09-14T01:30:00Z", zone))
    }

    @Test
    fun `local date is formatted without zone conversion`() {
        assertEquals("20/10/2026", formatDate("2026-10-20", zone))
    }

    @Test
    fun `local date time without zone is treated as UTC`() {
        assertEquals("13/09/2026", formatDate("2026-09-13T15:17:00", zone))
        assertEquals("13/09/2026 às 12:17", formatDateTime("2026-09-13T15:17:00", zone))
        assertEquals("13/09/2026 às 12:17", formatDateTime("2026-09-13T15:17:00.146", zone))
    }

    @Test
    fun `null blank and invalid return dash`() {
        assertEquals("—", formatDate(null, zone))
        assertEquals("—", formatDate("", zone))
        assertEquals("—", formatDate("abc", zone))
        assertEquals("—", formatDateTime(null, zone))
        assertEquals("—", formatDateTime("", zone))
        assertEquals("—", formatDateTime("abc", zone))
    }

    @Test
    fun `parseDateBr accepts brazilian and iso dates`() {
        assertEquals(LocalDate.of(2026, 10, 18), parseDateBr("18/10/2026"))
        assertEquals(LocalDate.of(2026, 10, 18), parseDateBr("2026-10-18"))
    }

    @Test
    fun `parseDateBr rejects invalid calendar dates blank and junk`() {
        assertNull(parseDateBr("31/02/2026"))
        assertNull(parseDateBr(""))
        assertNull(parseDateBr("abc"))
    }

    @Test
    fun `formatDateInput and parseDateBr round trip`() {
        val date = LocalDate.of(2026, 10, 18)
        assertEquals("18/10/2026", formatDateInput(date))
        assertEquals("", formatDateInput(null))
        assertEquals(date, parseDateBr(formatDateInput(date)))
        assertEquals("18/10/2026", formatDateInput(parseDateBr("18/10/2026")))
        assertEquals("18/10/2026", formatDateInput(parseDateBr("2026-10-18")))
    }

    @Test
    fun `parseDateBr accepts eight digits and rejects invalid calendar dates`() {
        assertEquals(LocalDate.of(2026, 10, 20), parseDateBr("20102026"))
        assertEquals(LocalDate.of(2026, 10, 20), parseDateBr("20/10/2026"))
        assertEquals("2026-10-20", parseDateBr("20102026").toString())
        assertNull(parseDateBr("31022026"))
        assertNull(parseDateBr("31/02/2026"))
        assertNull(parseDateBr("2026-02-31"))
        assertNull(dateFieldError("20102026"))
        assertEquals(DATE_INPUT_ERROR, dateFieldError("31022026"))
        assertNull(dateFieldError("2010"))
    }

    @Test
    fun `typing 20102026 keeps digits and parses iso date`() {
        var digits = ""
        for (char in "20102026") {
            digits = (digits + char).filter(Char::isDigit).take(8)
        }
        assertEquals("20102026", digits)
        assertEquals(LocalDate.of(2026, 10, 20), parseDateBr(digits))
        assertEquals("2026-10-20", parseDateBr(digits).toString())
        assertEquals("20102026", "20/10/2026".filter(Char::isDigit).take(8))
    }

    @Test
    fun `dateToDigits formats day month year without separators`() {
        assertEquals("20102026", dateToDigits(LocalDate.of(2026, 10, 20)))
        assertEquals("15092026", dateToDigits(LocalDate.of(2026, 9, 15)))
        assertEquals("", dateToDigits(null))
    }
}
