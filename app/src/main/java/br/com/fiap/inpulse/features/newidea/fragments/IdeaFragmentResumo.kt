package br.com.fiap.inpulse.features.newidea.fragments

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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.Categoria
import br.com.fiap.inpulse.data.repository.PredictionRepository
import br.com.fiap.inpulse.utils.CategoriaMapper
import kotlinx.coroutines.launch

class IdeaFragmentResumo(val predictionRepository: PredictionRepository) : Fragment(), IdeaInfoProvider {

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

        if (solucao.isNotBlank()) {
            buscarPredicoesDaIA(solucao)
        } else {
            Toast.makeText(context, "Nenhuma descrição informada!", Toast.LENGTH_LONG).show()
        }
    }

    private fun buscarPredicoesDaIA(descricao: String) {
        lifecycleScope.launch {
            mostrarEstadoDeLoading(true)
            try {
                val resultado = predictionRepository.getPredictions(descricao)
                val resultadoMapeado = CategoriaMapper.mapearPredicoes(resultado)
                mostrarEstadoDeSucesso(resultadoMapeado)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Falha ao buscar IA: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarEstadoDeSucesso(resultado: Map<String, Categoria>) {
        progressBar.visibility = View.GONE

        resultado["setor"]?.let { categoria ->
            iconeS.setImageResource(categoria.icone)
            iconeS.visibility = View.VISIBLE
        }
        resultado["objetivo"]?.let { categoria ->
            iconeO.setImageResource(categoria.icone)
            iconeO.visibility = View.VISIBLE
        }
        resultado["complexidade"]?.let { categoria ->
            iconeC.setImageResource(categoria.icone)
            iconeC.visibility = View.VISIBLE
        }
        resultado["urgencia"]?.let { categoria ->
            iconeU.setImageResource(categoria.icone)
            iconeU.visibility = View.VISIBLE
        }
    }

    private fun mostrarEstadoDeLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            iconeC.visibility = View.INVISIBLE
            iconeO.visibility = View.INVISIBLE
            iconeU.visibility = View.INVISIBLE
            iconeS.visibility = View.INVISIBLE
        }
    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        bundle.putString("resumoProblema", resumoProblemaTextView.text.toString())
        bundle.putString("resumoSolucao", resumoSolucaoTextView.text.toString())
        bundle.putString("etTitulo", etTituloEditText.text.toString())

        return bundle
    }
}