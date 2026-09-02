package br.com.inovagabv2.core.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aguia_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_KEY = stringPreferencesKey("user_session")

    val userSession: Flow<User?> = context.dataStore.data.map { preferences ->
        preferences[USER_KEY]?.let { json ->
            try {
                Json.decodeFromString<User>(json)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveSession(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = Json.encodeToString(User.serializer(), user)
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
        }
    }
}
