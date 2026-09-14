package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.data.remote.dto.common.PaginaResponseDto
import br.com.inovagabv2.data.remote.dto.idea.IdeiaResponseDto
import br.com.inovagabv2.data.remote.dto.idea.RemoteStatusIdeia
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Pagina

fun RemoteStatusIdeia.toDomain(): IdeaStatus = when (this) {
    RemoteStatusIdeia.ENVIADA -> IdeaStatus.ENVIADA
    RemoteStatusIdeia.EM_ANALISE -> IdeaStatus.EM_ANALISE
    RemoteStatusIdeia.APROVADA -> IdeaStatus.APROVADA
    RemoteStatusIdeia.REJEITADA -> IdeaStatus.REJEITADA
    RemoteStatusIdeia.ARQUIVADA -> IdeaStatus.ARQUIVADA
}

fun IdeaStatus.toRemote(): RemoteStatusIdeia = when (this) {
    IdeaStatus.ENVIADA -> RemoteStatusIdeia.ENVIADA
    IdeaStatus.EM_ANALISE -> RemoteStatusIdeia.EM_ANALISE
    IdeaStatus.APROVADA -> RemoteStatusIdeia.APROVADA
    IdeaStatus.REJEITADA -> RemoteStatusIdeia.REJEITADA
    IdeaStatus.ARQUIVADA -> RemoteStatusIdeia.ARQUIVADA
}

fun IdeiaResponseDto.toDomain(): Idea {
    return Idea(
        id = id,
        title = titulo,
        problem = problema,
        proposedSolution = solucaoProposta,
        expectedBenefits = beneficiosEsperados,
        category = categoria,
        strategyId = estrategiaId,
        authorId = autorId,
        authorName = "Autor não informado",
        status = status.toDomain(),
        priority = prioridade,
        evaluationJustification = justificativaAvaliacao,
        evaluatedById = avaliadoPorId,
        evaluatedAt = avaliadoEm,
        createdAt = criadoEm,
        updatedAt = atualizadoEm,
        version = versao
    )
}

fun PaginaResponseDto<IdeiaResponseDto>.toDomain(): Pagina<Idea> {
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
