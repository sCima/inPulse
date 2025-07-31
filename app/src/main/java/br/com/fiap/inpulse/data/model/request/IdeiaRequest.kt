package br.com.fiap.inpulse.data.model.request

data class IdeiaRequest (
    val nome: String,
    val problema: String,
    val descricao: String,
    val imagem: String?,
    val funcionario_id: Int,
    val categorias_id: List<Int>
)