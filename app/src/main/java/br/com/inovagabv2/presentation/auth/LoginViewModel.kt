package br.com.inovagabv2.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.util.toAuthUserMessage
import br.com.inovagabv2.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CONNECTING_HINT_DELAY_MS = 5_000L

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            if (_uiState.value.isLoading) return@launch
            _uiState.update { it.copy(isLoading = true, showConnectingHint = false, error = null) }
            val hintJob = launch {
                delay(CONNECTING_HINT_DELAY_MS)
                _uiState.update { state ->
                    if (state.isLoading) state.copy(showConnectingHint = true) else state
                }
            }
            try {
                authRepository.login(email.trim(), password)
                    .onSuccess { user ->
                        _uiState.update { it.copy(successUser = user, error = null) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(error = error.toAuthUserMessage()) }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.toAuthUserMessage()) }
            } finally {
                hintJob.cancel()
                _uiState.update { it.copy(isLoading = false, showConnectingHint = false) }
            }
        }
    }
}
