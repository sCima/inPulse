package br.com.fiap.inpulse.data.model.response

data class ItemResponse(
    val id: Int,
    val nome: String,
    val descricao: String,
    val preco: Int,
    val funcionarios: List<Int>,
    val tier: String
)