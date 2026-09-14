package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

enum class StrategyStatus(val displayName: String) {
    RASCUNHO("Rascunho"),
    ATIVA("Ativa"),
    INATIVA("Inativa"),
    ARQUIVADA("Arquivada")
}

@Serializable
data class Strategy(
    val id: String,
    val title: String,
    val description: String,
    val date: String = "",
    val category: String? = null,
    val campaign: String? = null,
    val status: StrategyStatus = StrategyStatus.RASCUNHO,
    val createdById: String? = null,
    val updatedById: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val version: Long? = null
) {
    val isPublished: Boolean get() = status == StrategyStatus.ATIVA
    val publishedDate: String? get() = if (isPublished) (date.ifBlank { createdAt }) else null
}
