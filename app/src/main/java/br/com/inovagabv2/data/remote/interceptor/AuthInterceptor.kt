package br.com.inovagabv2.data.remote.interceptor

import br.com.inovagabv2.core.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        val isAuthEndpoint = path.contains("/api/auth/login") || path.contains("/api/auth/cadastro")

        val token = runBlocking { sessionManager.jwtToken.firstOrNull() }

        val authenticatedRequest = if (!token.isNullOrBlank() && !isAuthEndpoint) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        val response = chain.proceed(authenticatedRequest)

        if (response.code == 401 && !isAuthEndpoint && !token.isNullOrBlank()) {
            runBlocking { sessionManager.clearSessionIfTokenMatches(token) }
        }

        return response
    }
}
