package br.com.inovagabv2.core.util

import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val DATE_INPUT_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    .withResolverStyle(ResolverStyle.STRICT)
private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")
private const val EMPTY_DATE = "—"
const val DATE_INPUT_ERROR = "Informe uma data válida (DD/MM/AAAA)."

fun parseDateBr(input: String): LocalDate? {
    val value = input.trim()
    if (value.isEmpty()) return null
    return try {
        when {
            '/' in value -> LocalDate.parse(value, DATE_INPUT_BR)
            value.length == 8 && value.all { it.isDigit() } -> {
                LocalDate.of(
                    value.substring(4, 8).toInt(),
                    value.substring(2, 4).toInt(),
                    value.substring(0, 2).toInt()
                )
            }
            else -> LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    } catch (_: DateTimeException) {
        null
    }
}

fun formatDateInput(date: LocalDate?): String = date?.format(DATE_FORMAT).orEmpty()

fun dateToDigits(date: LocalDate?): String {
    if (date == null) return ""
    val day = date.dayOfMonth.toString().padStart(2, '0')
    val month = date.monthValue.toString().padStart(2, '0')
    val year = date.year.toString().padStart(4, '0')
    return day + month + year
}

fun dateFieldError(input: String): String? {
    if (input.count { it.isDigit() } != 8) return null
    return if (parseDateBr(input) == null) DATE_INPUT_ERROR else null
}

fun formatDate(iso: String?, zone: ZoneId = ZoneId.systemDefault()): String {
    return try {
        when (val parsed = parseIso(iso)) {
            is ParsedIso.ParsedMoment -> parsed.instant.atZone(zone).toLocalDate().format(DATE_FORMAT)
            is ParsedIso.ParsedDateOnly -> parsed.date.format(DATE_FORMAT)
            null -> EMPTY_DATE
        }
    } catch (_: Exception) {
        EMPTY_DATE
    }
}

fun formatDateTime(iso: String?, zone: ZoneId = ZoneId.systemDefault()): String {
    return try {
        when (val parsed = parseIso(iso)) {
            is ParsedIso.ParsedMoment -> parsed.instant.atZone(zone).format(DATE_TIME_FORMAT)
            is ParsedIso.ParsedDateOnly -> parsed.date.atStartOfDay().format(DATE_TIME_FORMAT)
            null -> EMPTY_DATE
        }
    } catch (_: Exception) {
        EMPTY_DATE
    }
}

private sealed interface ParsedIso {
    data class ParsedMoment(val instant: Instant) : ParsedIso
    data class ParsedDateOnly(val date: LocalDate) : ParsedIso
}

private fun parseIso(iso: String?): ParsedIso? {
    val value = iso?.trim().orEmpty()
    if (value.isEmpty()) return null

    parseInstant(value)?.let { return ParsedIso.ParsedMoment(it) }
    parseOffsetDateTime(value)?.let { return ParsedIso.ParsedMoment(it) }
    parseLocalDateTimeAsUtc(value)?.let { return ParsedIso.ParsedMoment(it) }
    parseLocalDate(value)?.let { return ParsedIso.ParsedDateOnly(it) }
    return null
}

private fun parseInstant(value: String): Instant? {
    return try {
        Instant.parse(value)
    } catch (_: DateTimeParseException) {
        null
    }
}

private fun parseOffsetDateTime(value: String): Instant? {
    return try {
        OffsetDateTime.parse(value).toInstant()
    } catch (_: DateTimeParseException) {
        null
    }
}

private fun parseLocalDateTimeAsUtc(value: String): Instant? {
    return try {
        LocalDateTime.parse(value).toInstant(ZoneOffset.UTC)
    } catch (_: DateTimeParseException) {
        null
    }
}

private fun parseLocalDate(value: String): LocalDate? {
    return try {
        LocalDate.parse(value)
    } catch (_: DateTimeParseException) {
        null
    }
}
