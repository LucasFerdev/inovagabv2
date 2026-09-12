package br.com.inovagabv2.data.repository

import br.com.inovagabv2.core.session.FakeSessionManager
import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AuthRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var sessionManager: FakeSessionManager
    private lateinit var repository: AuthRepositoryImpl
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        val api = retrofit.create(InovaGabApi::class.java)
        sessionManager = FakeSessionManager()
        repository = AuthRepositoryImpl(api, sessionManager, json)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login should save user and token on success`() = runBlocking {
        val successJsonResponse = """
            {
              "token": "jwt_token_12345",
              "tipo": "Bearer",
              "expiresIn": 3600,
              "usuario": {
                "id": "u123",
                "nome": "João Silva",
                "email": "joao@aguia.com",
                "empresa": "Águia Branca",
                "role": "OPERADOR",
                "ativo": true
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(successJsonResponse))

        val result = repository.login("joao@aguia.com", "senha1234")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("u123", user?.id)
        assertEquals("João Silva", user?.name)
        assertEquals(Role.OPERADOR, user?.role)

        // Verify session persistence
        val savedUser = sessionManager.userSession.first()
        val savedToken = sessionManager.getToken()
        assertEquals("u123", savedUser?.id)
        assertEquals("jwt_token_12345", savedToken)
    }

    @Test
    fun `login should return error message from backend when request fails`() = runBlocking {
        val errorJsonResponse = """
            {
              "timestamp": "2026-01-01T00:00:00Z",
              "status": 401,
              "erro": "Unauthorized",
              "mensagem": "E-mail ou senha incorretos.",
              "path": "/api/auth/login"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(401).setBody(errorJsonResponse))

        val result = repository.login("joao@aguia.com", "errada")

        assertTrue(result.isFailure)
        assertEquals("E-mail ou senha incorretos.", result.exceptionOrNull()?.message)
        assertNull(sessionManager.getToken())
    }

    @Test
    fun `register should perform cadastro and automatically login`() = runBlocking {
        val cadastroJsonResponse = """
            {
              "id": "u999",
              "nome": "Novo Usuario",
              "email": "novo@aguia.com",
              "empresa": "Águia Branca",
              "role": "OPERADOR",
              "ativo": true
            }
        """.trimIndent()

        val loginJsonResponse = """
            {
              "token": "jwt_token_new_user",
              "tipo": "Bearer",
              "expiresIn": 3600,
              "usuario": {
                "id": "u999",
                "nome": "Novo Usuario",
                "email": "novo@aguia.com",
                "empresa": "Águia Branca",
                "role": "OPERADOR",
                "ativo": true
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(cadastroJsonResponse))
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(loginJsonResponse))

        val result = repository.register(
            nome = "Novo Usuario",
            email = "novo@aguia.com",
            senha = "senha_segura_123",
            empresa = "Águia Branca"
        )

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("u999", user?.id)
        assertEquals("Novo Usuario", user?.name)

        val request1 = mockWebServer.takeRequest()
        assertEquals("/api/auth/cadastro", request1.path)
        val requestBody1 = request1.body.readUtf8()
        assertFalse("Request body must not contain role", requestBody1.contains("\"role\""))

        val request2 = mockWebServer.takeRequest()
        assertEquals("/api/auth/login", request2.path)

        assertEquals("jwt_token_new_user", sessionManager.getToken())
    }

    @Test
    fun `register with access code sends code normalized and stores user role GESTOR`() = runBlocking {
        val cadastroJsonResponse = """
            {
              "id": "u888",
              "nome": "Mariana Gestora",
              "email": "mariana@aguia.com",
              "empresa": "Águia Branca",
              "role": "GESTOR",
              "ativo": true
            }
        """.trimIndent()

        val loginJsonResponse = """
            {
              "token": "jwt_gestor_token",
              "tipo": "Bearer",
              "expiresIn": 3600,
              "usuario": {
                "id": "u888",
                "nome": "Mariana Gestora",
                "email": "mariana@aguia.com",
                "empresa": "Águia Branca",
                "role": "GESTOR",
                "ativo": true
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(cadastroJsonResponse))
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(loginJsonResponse))

        val result = repository.register(
            nome = "Mariana Gestora",
            email = "mariana@aguia.com",
            senha = "senha_segura_123",
            empresa = "Águia Branca",
            codigoAcesso = "  GEST2026  "
        )

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(Role.GESTOR, user?.role)

        val request1 = mockWebServer.takeRequest()
        val requestBody = request1.body.readUtf8()
        assertTrue("Request contains normalized codigoAcesso", requestBody.contains("\"codigoAcesso\":\"GEST2026\""))
        assertFalse("Request does not contain role", requestBody.contains("\"role\""))

        // Verify session user role is GESTOR
        val savedUser = sessionManager.userSession.first()
        assertEquals(Role.GESTOR, savedUser?.role)
    }

    @Test
    fun `register with invalid access code returns 400 error message from backend`() = runBlocking {
        val error400Body = """
            {
              "timestamp": "2026-01-01T00:00:00Z",
              "status": 400,
              "erro": "Bad Request",
              "mensagem": "Código de acesso inválido.",
              "path": "/api/auth/cadastro"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody(error400Body))

        val result = repository.register(
            nome = "Usuario Teste",
            email = "teste@aguia.com",
            senha = "senha_segura_123",
            empresa = "Águia Branca",
            codigoAcesso = "CODIGO_ERRADO"
        )

        assertTrue(result.isFailure)
        assertEquals("Código de acesso inválido.", result.exceptionOrNull()?.message)
        assertNull(sessionManager.getToken())
    }

    @Test
    fun `logout should remove saved user and token`() = runBlocking {
        sessionManager.saveSession(
            user = User("u1", "Name", "e@mail.com", Role.OPERADOR),
            token = "jwt_token_to_remove"
        )

        repository.logout()

        assertNull(sessionManager.userSession.first())
        assertNull(sessionManager.getToken())
    }

    @Test
    fun `login should return helpful connection error message when network fails`() = runBlocking {
        mockWebServer.shutdown() // Simulates server being turned off or unreachable

        val result = repository.login("joao@aguia.com", "senha1234")

        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message
        assertTrue(
            errorMessage?.contains("backend está ligado") == true ||
            errorMessage?.contains("API_BASE_URL") == true
        )
    }
}
