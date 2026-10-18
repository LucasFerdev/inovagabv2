package br.com.inovagabv2.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
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
}
