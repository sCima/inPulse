package br.com.fiap.inpulse.data.model.response

data class IdeiaResponse (
    val ideia_id: Int,
    val nome: String,
    val problema: String,
    val descricao: String,
    val imagem: String,
    val data: String,
    var curtidas: Int,
    val funcionario_nome: ProgramaFuncionario,
    val programas_nome: List<ProgramaFuncionario>,
    val categoriasIcone: List<String>,
    val contribuicoes: List<Contribuicao>,
    var valorOrdenacao: String? = null
) {
    // Construtor auxiliar para converter de IdeiaFuncionario para IdeiaResponse
    constructor(
        ideiaFuncionario: IdeiaFuncionario,
        funcionarioNome: ProgramaFuncionario,
        contribuicoesPadrao: List<Contribuicao> = emptyList(), // Default para lista vazia,
    ) : this(
        ideia_id = ideiaFuncionario.ideia_id,
        nome = ideiaFuncionario.nome,
        problema = ideiaFuncionario.problema,
        descricao = ideiaFuncionario.descricao,
        imagem = ideiaFuncionario.imagem.toString(),
        data = ideiaFuncionario.data,
        curtidas = ideiaFuncionario.curtidas,
        funcionario_nome = funcionarioNome, // Preenchido com o nome do FuncionarioResponse
        programas_nome = ideiaFuncionario.programas_nome,
        categoriasIcone = ideiaFuncionario.categoriasIcone,
        contribuicoes = contribuicoesPadrao // Contribuições não vem em IdeiaFuncionario
    )
}

data class Contribuicao(
    val coment: String,
    val nomeAutor: String
)