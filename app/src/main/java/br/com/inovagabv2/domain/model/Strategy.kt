package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Strategy(
    val id: String,
    val title: String,
    val description: String,
    val objectives: List<String>,
    val isPublished: Boolean = true,
    val publishedDate: String? = null,
    val createdAt: String,
    val updatedAt: String
)
