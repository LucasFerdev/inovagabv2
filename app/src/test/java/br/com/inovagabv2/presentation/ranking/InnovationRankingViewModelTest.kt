package br.com.inovagabv2.presentation.ranking

import br.com.inovagabv2.data.remote.api.InovaGabApi
import br.com.inovagabv2.data.repository.RankingRepositoryImpl
import br.com.inovagabv2.domain.model.MedalhaRanking
import br.com.inovagabv2.domain.model.RankingColaborador
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.RankingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class InnovationRankingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: InovaGabApi
    private lateinit var fakeRankingRepository: FakeRankingRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: InnovationRankingViewModel
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(InovaGabApi::class.java)

        fakeRankingRepository = FakeRankingRepository()
        fakeAuthRepository = FakeAuthRepository()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        Dispatchers.resetMain()
    }

    @Test
    fun `getRankingColaboradores calls GET endpoint and maps DTOs`() = runTest {
        val jsonResponse = """
            [
              {"posicao": 1, "nome": "Lucas Fernando", "empresa": "Viação Águia Branca", "ideiasAprovadas": 10, "ideiasImplementadas": 5, "medalha": "OURO"},
              {"posicao": 2, "nome": "Ana Paula", "empresa": "Viação Águia Branca", "ideiasAprovadas": 8, "ideiasImplementadas": 4, "medalha": "PRATA"},
              {"posicao": 3, "nome": "Rafael Santos", "empresa": "Viação Águia Branca", "ideiasAprovadas": 6, "ideiasImplementadas": 3, "medalha": "BRONZE"},
              {"posicao": 4, "nome": "Mariana Ferreira", "empresa": "Viação Águia Branca", "ideiasAprovadas": 4, "ideiasImplementadas": 2, "medalha": "SEM_MEDALHA"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val repo = RankingRepositoryImpl(api, json)
        val result = repo.getRankingColaboradores().first()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/ranking/colaboradores", recordedRequest.path)
        assertEquals("GET", recordedRequest.method)

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!
        assertEquals(4, list.size)
        assertEquals("Lucas Fernando", list.first().nome)
        assertEquals(MedalhaRanking.OURO, list.first().medalha)
    }

    @Test
    fun `HTTP 200 with empty list produces Empty state in ViewModel`() = runTest {
        fakeRankingRepository.rankingResult = Result.success(emptyList())
        viewModel = InnovationRankingViewModel(fakeRankingRepository, fakeAuthRepository)

        val state = viewModel.state.first { !it.isLoading }

        assertTrue(state.ranking.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `HTTP 500 error produces Error state in ViewModel and does NOT generate fake list`() = runTest {
        fakeRankingRepository.rankingResult = Result.failure(Exception("Serviço de ranking indisponível."))
        viewModel = InnovationRankingViewModel(fakeRankingRepository, fakeAuthRepository)

        val state = viewModel.state.first { !it.isLoading }

        assertTrue(state.ranking.isEmpty())
        assertEquals("Serviço de ranking indisponível.", state.error)
    }

    @Test
    fun `top 3 and rest of ranking are separated correctly`() = runTest {
        val list = listOf(
            RankingColaborador(1, "Lucas", "VAB", 10, 5, MedalhaRanking.OURO),
            RankingColaborador(2, "Ana", "VAB", 8, 4, MedalhaRanking.PRATA),
            RankingColaborador(3, "Rafael", "VAB", 6, 3, MedalhaRanking.BRONZE),
            RankingColaborador(4, "Mariana", "VAB", 4, 2, MedalhaRanking.SEM_MEDALHA)
        )
        fakeRankingRepository.rankingResult = Result.success(list)
        viewModel = InnovationRankingViewModel(fakeRankingRepository, fakeAuthRepository)

        val state = viewModel.state.first { !it.isLoading }

        assertNotNull(state.top1)
        assertEquals("Lucas", state.top1?.nome)
        assertEquals("Ana", state.top2?.nome)
        assertEquals("Rafael", state.top3?.nome)
        assertEquals(1, state.restOfRanking.size)
        assertEquals("Mariana", state.restOfRanking.first().nome)
    }

    private class FakeRankingRepository : RankingRepository {
        var rankingResult: Result<List<RankingColaborador>> = Result.success(emptyList())

        override fun getRankingColaboradores(): Flow<Result<List<RankingColaborador>>> {
            return flowOf(rankingResult)
        }
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String) = Result.success(User("u1", "Lucas", email, Role.OPERADOR))
        override suspend fun register(nome: String, email: String, senha: String, empresa: String, codigoAcesso: String?) = Result.success(User("u1", nome, email, Role.OPERADOR))
        override suspend fun logout() {}
        override fun getCurrentUser(): Flow<User?> = flowOf(User("u1", "Lucas", "lucas@aguia.com", Role.OPERADOR))
    }
}
