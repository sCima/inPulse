package br.com.fiap.inpulse.utils

import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.Categoria

object CategoriaMapper {

    private val categoriaPadrao = Categoria(0, "Indefinido", R.drawable.ic_launcher_foreground)

    private val categoriaErro = Categoria(-1, "Erro na IA", R.drawable.ic_launcher_foreground)

    internal val mapaSetor = mapOf(
        "atef" to Categoria(102, "Administração, TI & Finanças", R.drawable.administracao),
        "pel" to Categoria(103, "Produção e Logística", R.drawable.logistica),
        "geral" to Categoria(104, "Geral", R.drawable.geral),
        "ped" to Categoria(105, "Pesquisa & Desenvolvimento", R.drawable.ped)
    )

    internal val mapaObjetivo = mapOf(
        "oa" to Categoria(106, "Otimização/Automação", R.drawable.otimizacao),
        "inv" to Categoria(107, "Inovação", R.drawable.melhoria),
        "sustentabilidade" to Categoria(108, "Sustentabilidade", R.drawable.sustentabilidade)
    )

    internal val mapaComplexidade = mapOf(
        "baixa" to Categoria(109, "Baixa Complexidade", R.drawable.baixa),
        "media" to Categoria(110, "Média Complexidade", R.drawable.media),
        "alta" to Categoria(111, "Alta Complexidade", R.drawable.alta)
    )

    internal val mapaUrgencia = mapOf(
        "programada" to Categoria(112, "Programada", R.drawable.programada),
        "prioritaria" to Categoria(113, "Prioritária", R.drawable.prioritaria),
        "urgente" to Categoria(114, "Urgente", R.drawable.critica)
    )

    fun mapearPredicoes(predictions: Map<String, String>): Map<String, Categoria> {

        fun getCategoriaPara(chave: String, mapa: Map<String, Categoria>): Categoria {
            val labelOriginal = predictions[chave]

            if (labelOriginal?.startsWith("Erro") == true) {
                return categoriaErro
            }

            val label = labelOriginal?.trim()?.lowercase()

            return mapa[label] ?: categoriaPadrao
        }

        return mapOf(
            "setor" to getCategoriaPara("setor", mapaSetor),
            "objetivo" to getCategoriaPara("objetivo", mapaObjetivo),
            "complexidade" to getCategoriaPara("complexidade", mapaComplexidade),
            "urgencia" to getCategoriaPara("urgencia", mapaUrgencia)
        )
    }
}