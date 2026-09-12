package br.com.inovagabv2.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
        }
    }
}
