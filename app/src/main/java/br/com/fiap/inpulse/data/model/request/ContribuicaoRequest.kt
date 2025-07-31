package br.com.fiap.inpulse.data.model.request

data class ContribuicaoRequest (
    val comentario: String,
    val funcionario_id: Int,
    val ideia_id: Int,
)