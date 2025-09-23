package br.com.fiap.inpulse.utils

import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.Categoria

object CategoriaMapper {

    private val categoriaPadrao = Categoria(0, "Indefinido", R.drawable.ic_launcher_foreground)

    internal val mapaSetor = mapOf(
        "atef" to Categoria(1, "Administração, TI & Finanças", R.drawable.administracao),
        "pel" to Categoria(2, "Produção e Logística", R.drawable.logistica),
        "geral" to Categoria(3, "Geral", R.drawable.geral),
        "ped" to Categoria(4, "Pesquisa & Desenvolvimento", R.drawable.ped)
    )

    internal val mapaObjetivo = mapOf(
        "oa" to Categoria(5, "Otimização/Automação", R.drawable.otimizacao),
        "inv" to Categoria(6, "Inovação", R.drawable.melhoria),
        "sustentabilidade" to Categoria(7, "Sustentabilidade", R.drawable.sustentabilidade)
    )

    internal val mapaComplexidade = mapOf(
        "baixa" to Categoria(8, "Baixa Complexidade", R.drawable.baixa),
        "media" to Categoria(9, "Média Complexidade", R.drawable.media),
        "alta" to Categoria(10, "Alta Complexidade", R.drawable.alta)
    )

    internal val mapaUrgencia = mapOf(
        "programada" to Categoria(11, "Programada", R.drawable.programada),
        "prioritaria" to Categoria(12, "Prioritária", R.drawable.prioritaria),
        "urgente" to Categoria(13, "Urgente", R.drawable.critica)
    )

    fun mapearPredicoes(predictions: Map<String, String>): Map<String, Categoria> {
        return mapOf(
            "setor" to (mapaSetor[predictions["setor"]] ?: categoriaPadrao),
            "objetivo" to (mapaObjetivo[predictions["objetivo"]] ?: categoriaPadrao),
            "complexidade" to (mapaComplexidade[predictions["complexidade"]] ?: categoriaPadrao),
            "urgencia" to (mapaUrgencia[predictions["urgencia"]] ?: categoriaPadrao)
        )
    }
}