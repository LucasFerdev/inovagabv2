package br.com.inovagabv2.data.mapper

import br.com.inovagabv2.data.remote.dto.dashboard.DashboardEstrategiaResponseDto
import br.com.inovagabv2.data.remote.dto.dashboard.DashboardProjetoResponseDto
import br.com.inovagabv2.data.remote.dto.dashboard.DashboardResumoResponseDto
import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.model.DashboardProjectDetails
import br.com.inovagabv2.domain.model.DashboardStrategyDetails

fun DashboardResumoResponseDto.toDomain(): DashboardData {
    return DashboardData(
        totalProjetos = totalProjetos,
        totalPlanejado = totalPlanejado,
        totalEmAndamento = totalEmAndamento,
        totalPausado = totalPausado,
        totalConcluido = totalConcluido,
        totalCancelado = totalCancelado,
        investimentoTotal = investimentoTotal,
        retornoFinanceiroTotal = retornoFinanceiroTotal,
        lucroObtido = lucroObtido,
        roiPercentual = roiPercentual,
        progressoMedio = progressoMedio,
        ganhoMedioProdutividade = ganhoMedioProdutividade,
        projetosAtrasados = projetosAtrasados,
        ideiasEnviadas = ideiasEnviadas,
        ideiasEmAnalise = ideiasEmAnalise,
        ideiasAprovadas = ideiasAprovadas,
        ideiasRejeitadas = ideiasRejeitadas,
        projetosPorStatus = projetosPorStatus.mapKeys { it.key.toDomain() },
        ideiasPorStatus = ideiasPorStatus.mapKeys { it.key.toDomain() }
    )
}

fun DashboardEstrategiaResponseDto.toDomain(): DashboardStrategyDetails {
    return DashboardStrategyDetails(
        estrategiaId = estrategiaId,
        titulo = titulo,
        status = status.toDomain(),
        quantidadeIdeias = quantidadeIdeias,
        ideiasAprovadas = ideiasAprovadas,
        quantidadeProjetos = quantidadeProjetos,
        investimento = investimento,
        retorno = retorno,
        lucro = lucro,
        roiPercentual = roiPercentual,
        progressoMedio = progressoMedio,
        produtividadeMedia = produtividadeMedia,
        projetosAtrasados = projetosAtrasados
    )
}

fun DashboardProjetoResponseDto.toDomain(): DashboardProjectDetails {
    return DashboardProjectDetails(
        id = id,
        nome = nome,
        descricao = descricao,
        estrategiaTitulo = estrategia?.titulo,
        ideiaOrigemTitulo = ideiaOrigem?.titulo,
        etapa = etapa.toDomain(),
        status = status.toDomain(),
        percentualProgresso = percentualProgresso,
        investimento = investimento,
        retornoFinanceiro = retornoFinanceiro,
        lucro = lucro,
        roiPercentual = roiPercentual,
        prazo = prazo,
        atrasado = atrasado,
        ganhoProdutividadePercentual = ganhoProdutividadePercentual,
        resultado = resultado
    )
}
