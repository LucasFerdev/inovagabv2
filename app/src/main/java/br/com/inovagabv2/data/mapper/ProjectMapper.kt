package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.data.remote.dto.common.PaginaResponseDto
import br.com.inovagabv2.data.remote.dto.project.*
import br.com.inovagabv2.domain.model.Pagina
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus

fun RemoteStatusProjeto.toDomain(): ProjectStatus = when (this) {
    RemoteStatusProjeto.PLANEJADO -> ProjectStatus.PLANEJADO
    RemoteStatusProjeto.EM_ANDAMENTO -> ProjectStatus.EM_ANDAMENTO
    RemoteStatusProjeto.PAUSADO -> ProjectStatus.PAUSADO
    RemoteStatusProjeto.CONCLUIDO -> ProjectStatus.CONCLUIDO
    RemoteStatusProjeto.CANCELADO -> ProjectStatus.CANCELADO
}

fun ProjectStatus.toRemote(): RemoteStatusProjeto = when (this) {
    ProjectStatus.PLANEJADO -> RemoteStatusProjeto.PLANEJADO
    ProjectStatus.EM_ANDAMENTO -> RemoteStatusProjeto.EM_ANDAMENTO
    ProjectStatus.PAUSADO -> RemoteStatusProjeto.PAUSADO
    ProjectStatus.CONCLUIDO -> RemoteStatusProjeto.CONCLUIDO
    ProjectStatus.CANCELADO -> RemoteStatusProjeto.CANCELADO
}

fun RemoteEtapaProjeto.toDomain(): ProjectStage = when (this) {
    RemoteEtapaProjeto.PLANEJAMENTO -> ProjectStage.PLANEJAMENTO
    RemoteEtapaProjeto.DESENVOLVIMENTO -> ProjectStage.DESENVOLVIMENTO
    RemoteEtapaProjeto.PILOTO -> ProjectStage.PILOTO
    RemoteEtapaProjeto.IMPLEMENTACAO -> ProjectStage.IMPLEMENTACAO
    RemoteEtapaProjeto.ENCERRAMENTO -> ProjectStage.ENCERRAMENTO
}

fun ProjectStage.toRemote(): RemoteEtapaProjeto = when (this) {
    ProjectStage.PLANEJAMENTO -> RemoteEtapaProjeto.PLANEJAMENTO
    ProjectStage.DESENVOLVIMENTO -> RemoteEtapaProjeto.DESENVOLVIMENTO
    ProjectStage.PILOTO -> RemoteEtapaProjeto.PILOTO
    ProjectStage.IMPLEMENTACAO -> RemoteEtapaProjeto.IMPLEMENTACAO
    ProjectStage.ENCERRAMENTO -> RemoteEtapaProjeto.ENCERRAMENTO
}

fun ProjetoResponseDto.toDomain(): Project {
    return Project(
        id = id,
        name = nome,
        description = descricao,
        estrategiaId = estrategiaId,
        ideiaOrigemId = ideiaOrigemId,
        stage = etapa.toDomain(),
        status = status.toDomain(),
        percentualProgresso = percentualProgresso,
        investment = investimento,
        deadline = prazo,
        retornoFinanceiro = retornoFinanceiro,
        ganhoProdutividadePercentual = ganhoProdutividadePercentual,
        resultado = resultado,
        gestorResponsavelId = gestorResponsavelId,
        createdAt = criadoEm,
        updatedAt = atualizadoEm,
        version = versao
    )
}

fun PaginaResponseDto<ProjetoResponseDto>.toDomain(): Pagina<Project> {
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
