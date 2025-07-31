package br.com.fiap.inpulse.features.newidea.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.R

class IdeaFragmentR : Fragment(), IdeaInfoProvider {

    private lateinit var resumoProblemaTextView: TextView
    private lateinit var resumoSolucaoTextView: TextView
    private lateinit var etTituloEditText: EditText
    private lateinit var imageConfirmationPreview: ImageView
    private lateinit var cardViewImagePreview: CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideia_r, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resumoProblemaTextView = view.findViewById(R.id.txt_resumo_problema)
        resumoSolucaoTextView = view.findViewById(R.id.txt_resumo_solucao)
        etTituloEditText = view.findViewById(R.id.et_nome_ideia)
        imageConfirmationPreview = view.findViewById(R.id.imageConfirmationPreview)
        cardViewImagePreview = view.findViewById(R.id.cardViewImagePreview)

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

    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        bundle.putString("resumoProblema", resumoProblemaTextView.text.toString())
        bundle.putString("resumoSolucao", resumoSolucaoTextView.text.toString())
        bundle.putString("etTitulo", etTituloEditText.text.toString())

        return bundle
    }
}