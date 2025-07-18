package br.com.fiap.inpulse.data.response

data class FuncionarioResponse(
    val funcionario_id: Int,
    val primeiro_nome: String,
    val ultimo_sobrenome: String,
    val email: String,
    val senha: String,
    val pontos: Int,
    val moedas: Int,
    val tier: String,
    val modo_anonimo: Boolean,
    val ideias: List<IdeiaFuncionario>,
    val programas: List<String>,
    val selos: List<String>,
    val logs: List<String>
)

data class IdeiaFuncionario(
    val ideia_id: Int,
    val nome: String,
    val problema: String,
    val descricao: String,
    val imagem: String,
    val data: String,
    val curtidas: Int,
    val programas_nome: List<String>,
    val categoriasIcone: List<String>
)
