package br.com.inovagabv2.core.session

import android.content.ContextWrapper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SessionManagerTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var dataStore: DataStore<Preferences>

    private class TestableSessionManager(
        private val customDataStore: DataStore<Preferences>
    ) : SessionManager(ContextWrapper(null)) {
        override val dataStore: DataStore<Preferences>
            get() = customDataStore
    }

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_session.preferences_pb") }
        )
    }

    @Test
    fun `saveSession should persist user and JWT token`() = testScope.runTest {
        val sessionManager = TestableSessionManager(dataStore)
        val user = User("u123", "João Silva", "joao@aguia.com", Role.OPERADOR)
        val token = "jwt_token_abc"

        sessionManager.saveSession(user, token)

        val savedUser = sessionManager.userSession.first()
        val savedToken = sessionManager.getToken()
        val onboardingCompleted = sessionManager.isOnboardingCompleted.first()

        assertEquals("u123", savedUser?.id)
        assertEquals("João Silva", savedUser?.name)
        assertEquals(Role.OPERADOR, savedUser?.role)
        assertEquals("jwt_token_abc", savedToken)
        assertTrue(onboardingCompleted)
    }

    @Test
    fun `session restored after recreation of SessionManager`() = testScope.runTest {
        val sessionManager1 = TestableSessionManager(dataStore)
        val user = User("u456", "Mariana Costa", "mariana@aguia.com", Role.GESTOR)
        val token = "jwt_token_xyz"

        sessionManager1.saveSession(user, token)

        // Simulate app restart by creating a new SessionManager instance pointing to the same DataStore
        val sessionManager2 = TestableSessionManager(dataStore)

        val restoredUser = sessionManager2.userSession.first()
        val restoredToken = sessionManager2.getToken()
        val appInitState = sessionManager2.appInitState.first()

        assertEquals("u456", restoredUser?.id)
        assertEquals("jwt_token_xyz", restoredToken)
        assertTrue(appInitState is AppInitState.Authenticated)
        assertEquals(user, (appInitState as AppInitState.Authenticated).user)
    }

    @Test
    fun `logout removes session but preserves onboarding completed`() = testScope.runTest {
        val sessionManager = TestableSessionManager(dataStore)
        val user = User("u789", "Carlos Mendes", "carlos@aguia.com", Role.LIDERANCA)
        val token = "jwt_token_123"

        sessionManager.saveSession(user, token)
        sessionManager.clearSession()

        val currentUser = sessionManager.userSession.first()
        val currentToken = sessionManager.getToken()
        val onboardingCompleted = sessionManager.isOnboardingCompleted.first()
        val appInitState = sessionManager.appInitState.first()

        assertNull(currentUser)
        assertNull(currentToken)
        assertTrue(onboardingCompleted)
        assertTrue(appInitState is AppInitState.Unauthenticated)
        assertTrue((appInitState as AppInitState.Unauthenticated).onboardingCompleted)
    }
}
