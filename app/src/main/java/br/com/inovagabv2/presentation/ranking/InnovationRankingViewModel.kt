package br.com.inovagabv2.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inovagabv2.domain.model.RankingColaborador
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.domain.repository.AuthRepository
import br.com.inovagabv2.domain.repository.RankingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InnovationRankingState(
    val ranking: List<RankingColaborador> = emptyList(),
    val top1: RankingColaborador? = null,
    val top2: RankingColaborador? = null,
    val top3: RankingColaborador? = null,
    val restOfRanking: List<RankingColaborador> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showCriteriaDialog: Boolean = false
)

@HiltViewModel
class InnovationRankingViewModel @Inject constructor(
    private val rankingRepository: RankingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _state = MutableStateFlow(InnovationRankingState())
    val state: StateFlow<InnovationRankingState> = _state.asStateFlow()

    init {
        loadRanking()
    }

    fun loadRanking() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            rankingRepository.getRankingColaboradores().collect { result ->
                if (result.isSuccess) {
                    val list = result.getOrNull() ?: emptyList()
                    val top1 = list.find { it.posicao == 1 } ?: list.getOrNull(0)
                    val top2 = list.find { it.posicao == 2 } ?: list.getOrNull(1)
                    val top3 = list.find { it.posicao == 3 } ?: list.getOrNull(2)
                    val rest = list.filter { it.posicao > 3 }

                    _state.update {
                        it.copy(
                            ranking = list,
                            top1 = top1,
                            top2 = top2,
                            top3 = top3,
                            restOfRanking = rest,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.exceptionOrNull()?.message ?: "Erro ao carregar o ranking."
                        )
                    }
                }
            }
        }
    }

    fun onShowCriteriaDialog() = _state.update { it.copy(showCriteriaDialog = true) }
    fun onDismissCriteriaDialog() = _state.update { it.copy(showCriteriaDialog = false) }
}
