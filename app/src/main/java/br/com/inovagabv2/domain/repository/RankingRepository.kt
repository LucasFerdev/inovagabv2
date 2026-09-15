package br.com.inovagabv2.domain.repository

import br.com.inovagabv2.domain.model.RankingColaborador
import kotlinx.coroutines.flow.Flow

interface RankingRepository {
    fun getRankingColaboradores(): Flow<Result<List<RankingColaborador>>>
}
