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

        if (path.contains("/api/auth/login") || path.contains("/api/auth/cadastro")) {
            return chain.proceed(request)
        }

        val token = runBlocking { sessionManager.jwtToken.firstOrNull() }

        return if (!token.isNullOrBlank()) {
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(request)
        }
    }
}
