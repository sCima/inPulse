package br.com.fiap.inpulse.data.request

data class FuncionarioRequest(
    val primeiro_nome: String,
    val ultimo_sobrenome: String,
    val email: String,
    val senha: String,
    val modo_anonimo: Boolean
)