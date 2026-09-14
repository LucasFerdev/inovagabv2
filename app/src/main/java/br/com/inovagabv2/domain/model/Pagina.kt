package br.com.inovagabv2.domain.model

data class Pagina<T>(
    val conteudo: List<T>,
    val pagina: Int,
    val tamanho: Int,
    val totalElementos: Long,
    val totalPaginas: Int,
    val primeira: Boolean,
    val ultima: Boolean
)
