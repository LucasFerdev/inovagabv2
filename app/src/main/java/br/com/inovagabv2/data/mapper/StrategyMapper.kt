package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.data.remote.dto.common.PaginaResponseDto
import br.com.inovagabv2.data.remote.dto.strategy.EstrategiaResponseDto
import br.com.inovagabv2.data.remote.dto.strategy.RemoteStatusEstrategia
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Strategy
import br.com.inovagabv2.domain.model.StrategyStatus

fun RemoteStatusEstrategia.toDomain(): StrategyStatus = when (this) {
    RemoteStatusEstrategia.RASCUNHO -> StrategyStatus.RASCUNHO
    RemoteStatusEstrategia.ATIVA -> StrategyStatus.ATIVA
    RemoteStatusEstrategia.INATIVA -> StrategyStatus.INATIVA
    RemoteStatusEstrategia.ARQUIVADA -> StrategyStatus.ARQUIVADA
}

fun StrategyStatus.toRemote(): RemoteStatusEstrategia = when (this) {
    StrategyStatus.RASCUNHO -> RemoteStatusEstrategia.RASCUNHO
    StrategyStatus.ATIVA -> RemoteStatusEstrategia.ATIVA
    StrategyStatus.INATIVA -> RemoteStatusEstrategia.INATIVA
    StrategyStatus.ARQUIVADA -> RemoteStatusEstrategia.ARQUIVADA
}

fun EstrategiaResponseDto.toDomain(): Strategy {
    return Strategy(
        id = id,
        title = titulo,
        description = descricao,
        date = data,
        category = categoria,
        campaign = campanha,
        status = status.toDomain(),
        createdById = criadoPorId,
        updatedById = atualizadoPorId,
        createdAt = criadoEm ?: "",
        updatedAt = atualizadoEm ?: "",
        version = versao
    )
}

fun PaginaResponseDto<EstrategiaResponseDto>.toDomain(): Pagina<Strategy> {
    return Pagina(
        conteudo = conteudo.map { it.toDomain() },
        pagina = pagina,
        tamanho = tamanho,
        totalElementos = totalElementos,
        totalPaginas = totalPaginas,
        primeira = primeira,
        ultima = ultima
    )
}
