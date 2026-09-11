package br.com.inovagabv2.core.session

import android.content.ContextWrapper
import br.com.inovagabv2.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

private class DummyContext : ContextWrapper(null)

class FakeSessionManager : SessionManager(DummyContext()) {

    private val _userSession = MutableStateFlow<User?>(null)
    override val userSession: Flow<User?> = _userSession.asStateFlow()

    private val _jwtToken = MutableStateFlow<String?>(null)
    override val jwtToken: Flow<String?> = _jwtToken.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(false)
    override val isOnboardingCompleted: Flow<Boolean> = _isOnboardingCompleted.asStateFlow()

    override val appInitState: Flow<AppInitState> = combine(
        _userSession,
        _jwtToken,
        _isOnboardingCompleted
    ) { user, token, onboardingDone ->
        if (user != null && !token.isNullOrBlank()) {
            AppInitState.Authenticated(user)
        } else {
            AppInitState.Unauthenticated(onboardingCompleted = onboardingDone)
        }
    }

    override suspend fun getToken(): String? {
        return _jwtToken.value
    }

    override suspend fun saveSession(user: User, token: String) {
        _userSession.value = user
        _jwtToken.value = token
        _isOnboardingCompleted.value = true
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        _isOnboardingCompleted.value = completed
    }

    override suspend fun clearSession() {
        _userSession.value = null
        _jwtToken.value = null
    }
}
