package br.com.fiap.inpulse.data.model.response

data class  ProgramaResponse (
    val programa_id: Int,
    val nome_programa: String,
    val descricao_programa: String,
    val dataInicio: String,
    val dataFim: String,
    val funcionarios_nome: List<String>,
    val ideias_nome: List<IdeiaPrograma>
)

data class IdeiaPrograma (
    val id: Int,
    val nome: String
)