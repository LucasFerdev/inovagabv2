package br.com.inovagabv2.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        acceptedTerms: Boolean
    ) {
        viewModelScope.launch {
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

            _uiState.update { it.copy(isLoading = true, error = null) }

            val empresa = companyUnit.ifBlank { "Águia Branca" }
            authRepository.register(
                nome = fullName,
                email = email,
                senha = password,
                empresa = empresa
            ).onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, isSuccess = true, createdUser = user) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }
}
