package br.com.inovagabv2.data.remote.api

import br.com.inovagabv2.data.remote.dto.LoginRequestDto
import br.com.inovagabv2.data.remote.dto.LoginResponseDto
import br.com.inovagabv2.data.remote.dto.RegisterRequestDto
import br.com.inovagabv2.data.remote.dto.UsuarioResponseDto
import br.com.inovagabv2.data.remote.dto.ai.AnaliseIaIdeiaResponseDto
import br.com.inovagabv2.data.remote.dto.common.PaginaResponseDto
import br.com.inovagabv2.data.remote.dto.dashboard.DashboardEstrategiaResponseDto
import br.com.inovagabv2.data.remote.dto.dashboard.DashboardProjetoResponseDto
import br.com.inovagabv2.data.remote.dto.dashboard.DashboardResumoResponseDto
import br.com.inovagabv2.data.remote.dto.idea.*
import br.com.inovagabv2.data.remote.dto.project.*
import br.com.inovagabv2.data.remote.dto.strategy.*
import retrofit2.Response
import retrofit2.http.*

interface InovaGabApi {

    // Auth
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

    // Ideias
    @POST("api/ideias")
    suspend fun criarIdeia(
        @Body request: CriarIdeiaRequestDto
    ): Response<IdeiaResponseDto>

    @GET("api/ideias/minhas")
    suspend fun listarMinhasIdeias(
        @Query("pagina") pagina: Int = 0,
        @Query("tamanho") tamanho: Int = 20
    ): Response<PaginaResponseDto<IdeiaResponseDto>>

    @GET("api/ideias")
    suspend fun listarIdeias(
        @Query("status") status: RemoteStatusIdeia? = null,
        @Query("categoria") categoria: String? = null,
        @Query("estrategiaId") estrategiaId: String? = null,
        @Query("prioridade") prioridade: Int? = null,
        @Query("pagina") pagina: Int = 0,
        @Query("tamanho") tamanho: Int = 20
    ): Response<PaginaResponseDto<IdeiaResponseDto>>

    @GET("api/ideias/{id}")
    suspend fun buscarIdeiaPorId(
        @Path("id") id: String
    ): Response<IdeiaResponseDto>

    @GET("api/ideias/{id}/historico")
    suspend fun consultarHistoricoIdeia(
        @Path("id") id: String
    ): Response<List<HistoricoIdeiaResponseDto>>

    @PUT("api/ideias/{id}")
    suspend fun atualizarIdeia(
        @Path("id") id: String,
        @Body request: AtualizarIdeiaRequestDto
    ): Response<IdeiaResponseDto>

    @DELETE("api/ideias/{id}")
    suspend fun arquivarIdeia(
        @Path("id") id: String
    ): Response<Unit>

    @PATCH("api/ideias/{id}/analisar")
    suspend fun analisarIdeia(
        @Path("id") id: String
    ): Response<IdeiaResponseDto>

    @PATCH("api/ideias/{id}/priorizar")
    suspend fun priorizarIdeia(
        @Path("id") id: String,
        @Body request: PriorizarIdeiaRequestDto
    ): Response<IdeiaResponseDto>

    @PATCH("api/ideias/{id}/aprovar")
    suspend fun aprovarIdeia(
        @Path("id") id: String
    ): Response<IdeiaResponseDto>

    @PATCH("api/ideias/{id}/rejeitar")
    suspend fun rejeitarIdeia(
        @Path("id") id: String,
        @Body request: RejeitarIdeiaRequestDto
    ): Response<IdeiaResponseDto>

    // Estratégias
    @POST("api/estrategias")
    suspend fun criarEstrategia(
        @Body request: CriarEstrategiaRequestDto
    ): Response<EstrategiaResponseDto>

    @GET("api/estrategias")
    suspend fun listarEstrategias(
        @Query("status") status: RemoteStatusEstrategia? = null,
        @Query("categoria") categoria: String? = null,
        @Query("campanha") campanha: String? = null,
        @Query("pagina") pagina: Int = 0,
        @Query("tamanho") tamanho: Int = 20
    ): Response<PaginaResponseDto<EstrategiaResponseDto>>

    @GET("api/estrategias/ativas")
    suspend fun listarEstrategiasAtivas(): Response<List<EstrategiaResponseDto>>

    @GET("api/estrategias/{id}")
    suspend fun buscarEstrategiaPorId(
        @Path("id") id: String
    ): Response<EstrategiaResponseDto>

    @GET("api/estrategias/{id}/historico")
    suspend fun consultarHistoricoEstrategia(
        @Path("id") id: String
    ): Response<List<HistoricoEstrategiaResponseDto>>

    @PUT("api/estrategias/{id}")
    suspend fun atualizarEstrategia(
        @Path("id") id: String,
        @Body request: AtualizarEstrategiaRequestDto
    ): Response<EstrategiaResponseDto>

    @PATCH("api/estrategias/{id}/ativar")
    suspend fun ativarEstrategia(
        @Path("id") id: String
    ): Response<EstrategiaResponseDto>

    @PATCH("api/estrategias/{id}/desativar")
    suspend fun desativarEstrategia(
        @Path("id") id: String
    ): Response<EstrategiaResponseDto>

    @DELETE("api/estrategias/{id}")
    suspend fun arquivarEstrategia(
        @Path("id") id: String
    ): Response<Unit>

    // IA
    @POST("api/ia/ideias/{ideiaId}/analisar")
    suspend fun analisarIdeiaIa(
        @Path("ideiaId") ideiaId: String,
        @Query("recalcular") recalcular: Boolean = false
    ): Response<AnaliseIaIdeiaResponseDto>

    @GET("api/ia/ideias/{ideiaId}/analise")
    suspend fun consultarAnaliseIa(
        @Path("ideiaId") ideiaId: String
    ): Response<AnaliseIaIdeiaResponseDto>

    // Projetos
    @POST("api/projetos")
    suspend fun criarProjeto(
        @Body request: CriarProjetoRequestDto
    ): Response<ProjetoResponseDto>

    @GET("api/projetos")
    suspend fun listarProjetos(
        @Query("status") status: RemoteStatusProjeto? = null,
        @Query("etapa") etapa: RemoteEtapaProjeto? = null,
        @Query("estrategiaId") estrategiaId: String? = null,
        @Query("gestorId") gestorId: String? = null,
        @Query("prazo") prazo: String? = null,
        @Query("pagina") pagina: Int = 0,
        @Query("tamanho") tamanho: Int = 20
    ): Response<PaginaResponseDto<ProjetoResponseDto>>

    @GET("api/projetos/{id}")
    suspend fun buscarProjetoPorId(
        @Path("id") id: String
    ): Response<ProjetoResponseDto>

    @GET("api/projetos/{id}/historico")
    suspend fun consultarHistoricoProjeto(
        @Path("id") id: String
    ): Response<List<HistoricoProjetoResponseDto>>

    @PUT("api/projetos/{id}")
    suspend fun atualizarProjeto(
        @Path("id") id: String,
        @Body request: AtualizarProjetoRequestDto
    ): Response<ProjetoResponseDto>

    @PATCH("api/projetos/{id}/progresso")
    suspend fun atualizarProgressoProjeto(
        @Path("id") id: String,
        @Body request: AtualizarProgressoProjetoRequestDto
    ): Response<ProjetoResponseDto>

    @PATCH("api/projetos/{id}/resultados")
    suspend fun registrarResultadosProjeto(
        @Path("id") id: String,
        @Body request: RegistrarResultadosProjetoRequestDto
    ): Response<ProjetoResponseDto>

    @PATCH("api/projetos/{id}/concluir")
    suspend fun concluirProjeto(
        @Path("id") id: String
    ): Response<ProjetoResponseDto>

    @DELETE("api/projetos/{id}")
    suspend fun cancelarProjeto(
        @Path("id") id: String
    ): Response<Unit>

    // Dashboard
    @GET("api/dashboard/resumo")
    suspend fun consultarDashboardResumo(): Response<DashboardResumoResponseDto>

    @GET("api/dashboard/estrategias/{estrategiaId}")
    suspend fun consultarDashboardEstrategia(
        @Path("estrategiaId") estrategiaId: String
    ): Response<DashboardEstrategiaResponseDto>

    @GET("api/dashboard/projetos/{projetoId}")
    suspend fun consultarDashboardProjeto(
        @Path("projetoId") projetoId: String
    ): Response<DashboardProjetoResponseDto>
}
