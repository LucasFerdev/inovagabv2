package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectMetric(
    val label: String,
    val value: String,
    val trend: String? = null // e.g. "+15% vs mês anterior"
)
