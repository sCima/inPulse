package br.com.fiap.inpulse.features.newidea.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
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
import br.com.fiap.inpulse.data.api.HuggingFaceClient
import br.com.fiap.inpulse.data.model.Categoria
import br.com.fiap.inpulse.data.model.ClassificationRequest
import br.com.fiap.inpulse.utils.CategoriaMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

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
        val imagemBase64 = arguments?.getString("imagemBase64")

        resumoProblemaTextView.text = problema
        resumoSolucaoTextView.text = solucao

        val tituloPreenchido = arguments?.getString("titulo_ideia")
        if (!tituloPreenchido.isNullOrEmpty()) {
            etTituloEditText.setText(tituloPreenchido)
        }

        if (!imagemBase64.isNullOrEmpty()) {
            try {
                val decodedString = Base64.decode(imagemBase64, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                imageConfirmationPreview.setImageBitmap(decodedByte)
                imageConfirmationPreview.visibility = View.VISIBLE
                cardViewImagePreview.visibility = View.VISIBLE
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                imageConfirmationPreview.visibility = View.GONE
                cardViewImagePreview.visibility = View.GONE
            }
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
                Toast.makeText(context, "Falha ao buscar IA: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                mostrarEstadoDeLoading(false)
            }
        }
    }

    private object ApiEndpoints {
        const val SETOR = "https://cy2aeyw3cnigqfrw.us-east-1.aws.endpoints.huggingface.cloud"
        const val COMPLEXIDADE = "https://zs44mp1ynav45ofp.us-east-1.aws.endpoints.huggingface.cloud"
        const val URGENCIA = "https://qr3emi68x7kg1lit.us-east-1.aws.endpoints.huggingface.cloud"
        const val OBJETIVO = "https://gjnz2uohqdloo5um.us-east-1.aws.endpoints.huggingface.cloud"
    }

    private suspend fun classificarTextoDaIdeia(texto: String): Map<String, String> {
        val request = ClassificationRequest(texto)

        val apiService = HuggingFaceClient.api

        suspend fun callApiAndGetLabel(url: String): String {
            val response = apiService.classify(url, request)

            if (response.isSuccessful) {
                return response.body()?.firstOrNull()?.firstOrNull()?.label ?: "Label não encontrado"
            } else {
                throw IOException("Erro na API ($url): ${response.code()} - ${response.errorBody()?.string()}")
            }
        }

        return coroutineScope {
            val setorJob = async(Dispatchers.IO) { callApiAndGetLabel(ApiEndpoints.SETOR) }
            val objetivoJob = async(Dispatchers.IO) { callApiAndGetLabel(ApiEndpoints.OBJETIVO) }
            val complexidadeJob = async(Dispatchers.IO) { callApiAndGetLabel(ApiEndpoints.COMPLEXIDADE) }
            val urgenciaJob = async(Dispatchers.IO) { callApiAndGetLabel(ApiEndpoints.URGENCIA) }

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

        categorias["setor"]?.let {
            iconeS.setImageResource(it.icone)
            TooltipCompat.setTooltipText(iconeS, it.nome)
        }
        categorias["objetivo"]?.let {
            iconeO.setImageResource(it.icone)
            TooltipCompat.setTooltipText(iconeO, it.nome)
        }
        categorias["complexidade"]?.let {
            iconeC.setImageResource(it.icone)
            TooltipCompat.setTooltipText(iconeC, it.nome)
        }
        categorias["urgencia"]?.let {
            iconeU.setImageResource(it.icone)
            TooltipCompat.setTooltipText(iconeU, it.nome)
        }
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