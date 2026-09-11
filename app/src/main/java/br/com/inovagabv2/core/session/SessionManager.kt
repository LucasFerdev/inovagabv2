package br.com.inovagabv2.core.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.inovagabv2.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "aguia_session")

@Singleton
open class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_KEY = stringPreferencesKey("user_session")
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_concluido")

    protected open val dataStore: DataStore<Preferences> by lazy {
        context.sessionDataStore
    }

    open val userSession: Flow<User?> by lazy {
        dataStore.data.map { preferences ->
            preferences[USER_KEY]?.let { json ->
                try {
                    Json.decodeFromString<User>(json)
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    open val jwtToken: Flow<String?> by lazy {
        dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    open val isOnboardingCompleted: Flow<Boolean> by lazy {
        dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: false
        }
    }

    open val appInitState: Flow<AppInitState> by lazy {
        dataStore.data.map { preferences ->
            val userJson = preferences[USER_KEY]
            val token = preferences[TOKEN_KEY]
            val onboardingCompleted = preferences[ONBOARDING_COMPLETED_KEY] ?: false

            val user = if (!userJson.isNullOrBlank() && !token.isNullOrBlank()) {
                try {
                    Json.decodeFromString<User>(userJson)
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }

            if (user != null) {
                AppInitState.Authenticated(user)
            } else {
                AppInitState.Unauthenticated(onboardingCompleted = onboardingCompleted)
            }
        }
    }

    open suspend fun getToken(): String? {
        return jwtToken.firstOrNull()
    }

    open suspend fun saveSession(user: User, token: String) {
        dataStore.edit { preferences ->
            preferences[USER_KEY] = Json.encodeToString(User.serializer(), user)
            preferences[TOKEN_KEY] = token
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    open suspend fun setOnboardingCompleted(completed: Boolean = true) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    open suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
            preferences.remove(TOKEN_KEY)
        }
    }
}
