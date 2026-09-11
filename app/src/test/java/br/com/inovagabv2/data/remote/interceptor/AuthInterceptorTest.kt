package br.com.inovagabv2.data.remote.interceptor

import br.com.inovagabv2.core.session.FakeSessionManager
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var sessionManager: FakeSessionManager
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        sessionManager = FakeSessionManager()
        val interceptor = AuthInterceptor(sessionManager)

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `intercept should NOT add Bearer token for login endpoint`() = runBlocking {
        sessionManager.saveSession(User("1", "User", "user@aguia.com", Role.OPERADOR), "sample_jwt_token")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val request = Request.Builder()
            .url(mockWebServer.url("/api/auth/login"))
            .post("{}".toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().close()

        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept should NOT add Bearer token for cadastro endpoint`() = runBlocking {
        sessionManager.saveSession(User("1", "User", "user@aguia.com", Role.OPERADOR), "sample_jwt_token")

        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody("{}"))

        val request = Request.Builder()
            .url(mockWebServer.url("/api/auth/cadastro"))
            .post("{}".toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().close()

        val recordedRequest = mockWebServer.takeRequest()
        assertNull(recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `intercept SHOULD add Bearer token for protected endpoints when token exists`() = runBlocking {
        val testToken = "secret_jwt_token_xyz"
        sessionManager.saveSession(User("1", "User", "user@aguia.com", Role.OPERADOR), testToken)

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val request = Request.Builder()
            .url(mockWebServer.url("/api/auth/me"))
            .method("GET", null)
            .build()

        client.newCall(request).execute().close()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("Bearer secret_jwt_token_xyz", recordedRequest.getHeader("Authorization"))
    }
}
