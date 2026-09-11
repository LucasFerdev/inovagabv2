package br.com.inovagabv2.data.remote.api

import br.com.inovagabv2.data.remote.dto.LoginRequestDto
import br.com.inovagabv2.data.remote.dto.LoginResponseDto
import br.com.inovagabv2.data.remote.dto.RegisterRequestDto
import br.com.inovagabv2.data.remote.dto.UsuarioResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface InovaGabApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    @POST("api/auth/cadastro")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<UsuarioResponseDto>

    @GET("api/auth/me")
    suspend fun getMe(): Response<UsuarioResponseDto>
}
