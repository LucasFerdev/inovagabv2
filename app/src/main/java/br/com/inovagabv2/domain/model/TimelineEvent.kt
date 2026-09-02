package br.com.inovagabv2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TimelineEvent(
    val title: String,
    val date: String,
    val description: String? = null,
    val isCompleted: Boolean = false
)
