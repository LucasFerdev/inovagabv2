package br.com.inovagabv2.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    fun completeOnboarding(onFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                sessionManager.setOnboardingCompleted(true)
                onFinished()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Falha local não encerra o app nem avança a navegação.
            }
        }
    }
}
