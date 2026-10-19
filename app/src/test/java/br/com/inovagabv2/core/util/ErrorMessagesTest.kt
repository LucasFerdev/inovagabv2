package br.com.inovagabv2.core.util

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMessagesTest {

    @Test
    fun `IOException vira mensagem de conexao`() {
        assertEquals(NO_CONNECTION_MESSAGE, IOException("failed to connect").toUserMessage())
        assertEquals(NO_CONNECTION_MESSAGE, UnknownHostException("api.example").toUserMessage())
        assertEquals(NO_CONNECTION_MESSAGE, InterruptedIOException("interrupted").toUserMessage())
        assertEquals(
            NO_CONNECTION_MESSAGE,
            Exception("falha", IOException("rede")).toUserMessage()
        )
    }

    @Test
    fun `SocketTimeoutException no auth indica servidor iniciando`() {
        assertEquals(SERVER_STARTING_MESSAGE, SocketTimeoutException("timeout").toAuthUserMessage())
        assertEquals(SERVER_STARTING_MESSAGE, InterruptedIOException("timeout").toAuthUserMessage())
        assertEquals(
            SERVER_STARTING_MESSAGE,
            Exception("falha", SocketTimeoutException("timeout")).toAuthUserMessage()
        )
        assertEquals(NO_CONNECTION_MESSAGE, InterruptedIOException("interrupted").toAuthUserMessage())
        assertEquals(SERVER_STARTING_MESSAGE, SocketTimeoutException("timeout").toUserMessage())
        assertEquals(SERVER_STARTING_MESSAGE, InterruptedIOException("timeout").toUserMessage())
        assertEquals(SERVER_STARTING_MESSAGE, InterruptedIOException("read timed out").toUserMessage())
        assertEquals(
            SERVER_STARTING_MESSAGE,
            Exception("falha", SocketTimeoutException("timeout")).toUserMessage()
        )
    }

    @Test
    fun `HTTP 403 e 5xx viram mensagens proprias`() {
        assertEquals(NO_PERMISSION_MESSAGE, http(403).toUserMessage())
        assertEquals(SERVER_UNAVAILABLE_MESSAGE, http(500).toUserMessage())
        assertEquals(SERVER_UNAVAILABLE_MESSAGE, http(503).toUserMessage())
        assertEquals(
            NO_PERMISSION_MESSAGE,
            Exception("Acesso negado (403).").toUserMessage()
        )
        assertEquals(
            SERVER_UNAVAILABLE_MESSAGE,
            Exception("Erro no servidor (500).").toUserMessage()
        )
    }

    @Test
    fun `mensagem amigavel e preservada e texto tecnico vira generico`() {
        assertEquals("Erro na API de aprovação", Exception("Erro na API de aprovação").toUserMessage())
        assertEquals("Código de acesso inválido.", Exception("Código de acesso inválido.").toUserMessage())
        assertEquals(
            "O serviço de IA está indisponível no momento.",
            Exception("O serviço de IA está indisponível no momento.").toUserMessage()
        )
        assertEquals(
            "Serviço de ranking indisponível.",
            Exception("Serviço de ranking indisponível.").toUserMessage()
        )
        assertEquals(
            GENERIC_ERROR_MESSAGE,
            Exception("Falha ao conectar. Verifique se o backend está ligado e se a API_BASE_URL está correta.").toUserMessage()
        )
        assertEquals(GENERIC_ERROR_MESSAGE, Exception("HTTP 404 Not Found").toUserMessage())
        assertEquals(GENERIC_ERROR_MESSAGE, RuntimeException().toUserMessage())
    }

    private fun http(code: Int): HttpException {
        return HttpException(Response.error<String>(code, "".toResponseBody(null)))
    }
}
