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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun register(
        fullName: String,
        email: String,
        companyUnit: String,
        password: String,
        confirmPassword: String,
        acceptedTerms: Boolean,
        accessCode: String? = null
    ) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            if (_uiState.value.isLoading) return@launch
            if (fullName.isBlank()) {
                _uiState.update { it.copy(error = "Informe seu nome completo.") }
                return@launch
            }
            if (email.isBlank()) {
                _uiState.update { it.copy(error = "Informe seu e-mail.") }
                return@launch
            }
            if (password.length < 8) {
                _uiState.update { it.copy(error = "A senha deve ter pelo menos 8 caracteres.") }
                return@launch
            }
            if (password != confirmPassword) {
                _uiState.update { it.copy(error = "As senhas não coincidem.") }
                return@launch
            }
            if (!acceptedTerms) {
                _uiState.update { it.copy(error = "Você precisa aceitar os Termos de Uso.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, showConnectingHint = false, error = null) }
            val hintJob = launch {
                delay(CONNECTING_HINT_DELAY_MS)
                _uiState.update { state ->
                    if (state.isLoading) state.copy(showConnectingHint = true) else state
                }
            }

            val empresa = companyUnit.ifBlank { "Águia Branca" }
            val normalizedCode = accessCode?.trim()?.ifBlank { null }

            try {
                authRepository.register(
                    nome = fullName,
                    email = email.trim(),
                    senha = password,
                    empresa = empresa,
                    codigoAcesso = normalizedCode
                ).onSuccess { user ->
                    _uiState.update { it.copy(isSuccess = true, createdUser = user, error = null) }
                }.onFailure { error ->
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
