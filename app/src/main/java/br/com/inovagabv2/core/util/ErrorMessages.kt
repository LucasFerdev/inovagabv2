package br.com.inovagabv2.core.util

import retrofit2.HttpException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

const val NO_CONNECTION_MESSAGE = "Sem conexão. Verifique sua internet e tente novamente."
const val NO_PERMISSION_MESSAGE = "Você não tem permissão para esta ação."
const val SERVER_UNAVAILABLE_MESSAGE = "O servidor está indisponível no momento. Tente novamente em instantes."
const val SERVER_STARTING_MESSAGE = "O servidor está iniciando. Aguarde alguns segundos e tente novamente."
const val GENERIC_ERROR_MESSAGE = "Algo deu errado. Tente novamente."
const val LOAD_FAILED_MESSAGE = "Não foi possível carregar"

private val statusInParentheses = Regex("""\((403|5\d{2})\)""")
private val httpStatus = Regex("""\bHTTP\s+(403|5\d{2})\b""", RegexOption.IGNORE_CASE)

fun Throwable.toAuthUserMessage(): String {
    if (isServerStartingTimeout()) return SERVER_STARTING_MESSAGE
    return toUserMessage()
}

internal fun Throwable.isServerStartingTimeout(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        when (current) {
            is SocketTimeoutException -> return true
            is InterruptedIOException -> {
                val text = current.message?.lowercase().orEmpty()
                if (text.contains("timeout") || text.contains("timed out")) return true
            }
        }
        current = current.cause
    }
    return false
}

fun Throwable.toUserMessage(): String {
    var current: Throwable? = this
    var http: HttpException? = null
    var network = false
    while (current != null) {
        when (current) {
            is HttpException -> if (http == null) http = current
            is IOException -> network = true
        }
        current = current.cause
    }
    if (http != null) {
        return when (val code = http.code()) {
            403 -> NO_PERMISSION_MESSAGE
            in 500..599 -> SERVER_UNAVAILABLE_MESSAGE
            else -> http.message().toFriendlyOrGeneric()
        }
    }
    if (isServerStartingTimeout()) return SERVER_STARTING_MESSAGE
    if (network) return NO_CONNECTION_MESSAGE
    return message.toFriendlyOrGeneric()
}

private fun String?.toFriendlyOrGeneric(): String {
    val text = this?.trim().orEmpty()
    if (text.isEmpty()) return GENERIC_ERROR_MESSAGE
    statusCode(text)?.let { code ->
        return when (code) {
            403 -> NO_PERMISSION_MESSAGE
            in 500..599 -> SERVER_UNAVAILABLE_MESSAGE
            else -> GENERIC_ERROR_MESSAGE
        }
    }
    if (isTechnical(text)) return GENERIC_ERROR_MESSAGE
    return text
}

private fun statusCode(text: String): Int? {
    val match = statusInParentheses.find(text) ?: httpStatus.find(text)
    return match?.groupValues?.get(1)?.toIntOrNull()
}

private fun isTechnical(text: String): Boolean {
    if (text.contains("API_BASE_URL")) return true
    if (text.contains("backend", ignoreCase = true)) return true
    if (Regex("""\bHTTP\b""").containsMatchIn(text)) return true
    if (text.contains("\tat ") || text.contains(".java:")) return true
    if (text.contains("Exception")) return true
    return false
}
