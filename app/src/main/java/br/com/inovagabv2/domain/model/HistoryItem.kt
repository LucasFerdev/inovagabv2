package br.com.inovagabv2.domain.model

data class HistoryItem(
    val id: String,
    val action: String,
    val dateTime: String,
    val justification: String? = null,
    val status: String? = null
)
