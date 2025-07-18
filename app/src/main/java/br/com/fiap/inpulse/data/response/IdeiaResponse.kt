package br.com.fiap.inpulse.data.response

import java.time.LocalDate

data class IdeiaResponse (
    val ideia_id: Int,
    val nome: String,
    val problema: String,
    val descricao: String,
    val imagem: String,
    val data: String,
    val curtidas: Int,
    val funcionario_nome: String,
    val programas_nome: List<String>,
    val categoriasIcone: List<String>,
    val contribuicoes: List<Contribuicao>
)

data class Contribuicao(
    val coment: String,
    val nomeAutor: String
)