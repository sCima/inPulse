package br.com.fiap.inpulse.features.newidea.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.AzureRetrofitClient
import br.com.fiap.inpulse.data.model.Categoria
import br.com.fiap.inpulse.data.model.ClassificationRequest
import br.com.fiap.inpulse.data.model.LabelScore
import br.com.fiap.inpulse.utils.CategoriaMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import coil.load

interface OnCategoriasMapeadasListener {
    fun onCategoriasProntas(ids: List<Int>)
}

class IdeaFragmentResumo : Fragment(), IdeaInfoProvider {

    private var listener: OnCategoriasMapeadasListener? = null

    private lateinit var resumoProblemaTextView: TextView
    private lateinit var resumoSolucaoTextView: TextView
    private lateinit var etTituloEditText: EditText
    private lateinit var imageConfirmationPreview: ImageView
    private lateinit var cardViewImagePreview: CardView
    private lateinit var iconeS: ImageView
    private lateinit var iconeO: ImageView
    private lateinit var iconeU: ImageView
    private lateinit var iconeC: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCategoriasMapeadasListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnCategoriasMapeadasListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideia_resumo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resumoProblemaTextView = view.findViewById(R.id.txt_resumo_problema)
        resumoSolucaoTextView = view.findViewById(R.id.txt_resumo_solucao)
        etTituloEditText = view.findViewById(R.id.et_nome_ideia)
        imageConfirmationPreview = view.findViewById(R.id.imageConfirmationPreview)
        cardViewImagePreview = view.findViewById(R.id.cardViewImagePreview)
        iconeC = view.findViewById(R.id.icone_complexidade)
        iconeU = view.findViewById(R.id.icone_urgencia)
        iconeS = view.findViewById(R.id.icone_setor)
        iconeO = view.findViewById(R.id.icone_objetivo)
        progressBar = view.findViewById(R.id.pbResumo)

        val problema = arguments?.getString("problema") ?: "Nenhum problema informado"
        val solucao = arguments?.getString("solucao") ?: "Nenhuma solucao informada"
        resumoProblemaTextView.text = problema
        resumoSolucaoTextView.text = solucao

        val tituloPreenchido = arguments?.getString("titulo_ideia")
        if (!tituloPreenchido.isNullOrEmpty()) {
            etTituloEditText.setText(tituloPreenchido)
        }

        val imageUrl = arguments?.getString("imageUrl")
        if (!imageUrl.isNullOrEmpty()) {
            cardViewImagePreview.visibility = View.VISIBLE
            imageConfirmationPreview.visibility = View.VISIBLE
            imageConfirmationPreview.load(imageUrl) { crossfade(true) }
        } else {
            imageConfirmationPreview.visibility = View.GONE
            cardViewImagePreview.visibility = View.GONE
        }

        val textoCompleto = "$problema. $solucao"
        if (textoCompleto.length > 5) {
            buscarPredicoesDaIA(textoCompleto)
        } else {
            Toast.makeText(context, "Nenhuma descrição informada!", Toast.LENGTH_LONG).show()
        }
    }

    private fun buscarPredicoesDaIA(descricao: String) {
        lifecycleScope.launch {
            mostrarEstadoDeLoading(true)
            try {
                val resultadoLabels = classificarTextoDaIdeia(descricao)
                val resultadoMapeado = CategoriaMapper.mapearPredicoes(resultadoLabels)

                mostrarEstadoDeSucesso(resultadoMapeado)

                val ids = resultadoMapeado.values.map { it.id }
                listener?.onCategoriasProntas(ids)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Falha inesperada ao buscar IA: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                mostrarEstadoDeLoading(false)
            }
        }
    }

    private object DeploymentNames {
        const val SETOR = "deploy-setor"
        const val COMPLEXIDADE = "deploy-complexidade"
        const val URGENCIA = "deploy-urgencia"
        const val OBJETIVO = "deploy-objetivo"
    }

    private suspend fun classificarTextoDaIdeia(texto: String): Map<String, String> {
        val request = ClassificationRequest(texto)
        val apiService = AzureRetrofitClient.api

        suspend fun extrairLabelDaResposta(deploymentName: String): String {
             try {
                val response = apiService.classifyWithHeader(deploymentName, request)

                    if (response.isSuccessful) {
                        val todasPredicoes = response.body().orEmpty()

                        if (todasPredicoes.isEmpty()) {
                            return "Label não encontrado"
                        }

                        var melhorPredicao = todasPredicoes[0]
                        for (predicao in todasPredicoes) {
                            if (predicao.score > melhorPredicao.score) {
                                melhorPredicao = predicao
                            }
                        }
                        return melhorPredicao.label
                    } else {
                    val erro = response.errorBody()?.string()
                    Log.e("AzureIA", "Erro API $deploymentName: $erro")
                    return "Erro: ${response.code()}"
                    }
            } catch (e: Exception) {
                Log.e("AzureIA", "Erro de conexão no $deploymentName", e)
                return "Erro de conexão"
            }
        }

        return coroutineScope {
            val setorJob = async(Dispatchers.IO) { extrairLabelDaResposta(DeploymentNames.SETOR) }
            val objetivoJob = async(Dispatchers.IO) { extrairLabelDaResposta(DeploymentNames.OBJETIVO) }
            val complexidadeJob = async(Dispatchers.IO) { extrairLabelDaResposta(DeploymentNames.COMPLEXIDADE) }
            val urgenciaJob = async(Dispatchers.IO) { extrairLabelDaResposta(DeploymentNames.URGENCIA) }

            mapOf(
                "setor" to setorJob.await(),
                "objetivo" to objetivoJob.await(),
                "complexidade" to complexidadeJob.await(),
                "urgencia" to urgenciaJob.await()
            )
        }
    }

    private fun mostrarEstadoDeSucesso(categorias: Map<String, Categoria>) {
        iconeS.visibility = View.VISIBLE
        iconeO.visibility = View.VISIBLE
        iconeU.visibility = View.VISIBLE
        iconeC.visibility = View.VISIBLE

        val categoriaSetor = categorias.get("setor")!!
        iconeS.setImageResource(categoriaSetor.icone)
        TooltipCompat.setTooltipText(iconeS, categoriaSetor.nome)

        val categoriaObjetivo = categorias.get("objetivo")!!
        iconeO.setImageResource(categoriaObjetivo.icone)
        TooltipCompat.setTooltipText(iconeO, categoriaObjetivo.nome)

        val categoriaComplexidade = categorias.get("complexidade")!!
        iconeC.setImageResource(categoriaComplexidade.icone)
        TooltipCompat.setTooltipText(iconeC, categoriaComplexidade.nome)

        val categoriaUrgencia = categorias.get("urgencia")!!
        iconeU.setImageResource(categoriaUrgencia.icone)
        TooltipCompat.setTooltipText(iconeU, categoriaUrgencia.nome)
    }

    private fun mostrarEstadoDeLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            iconeC.visibility = View.INVISIBLE
            iconeO.visibility = View.INVISIBLE
            iconeU.visibility = View.INVISIBLE
            iconeS.visibility = View.INVISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        bundle.putString("resumoProblema", resumoProblemaTextView.text.toString())
        bundle.putString("resumoSolucao", resumoSolucaoTextView.text.toString())
        bundle.putString("etTitulo", etTituloEditText.text.toString())
        return bundle
    }
}