package br.com.fiap.inpulse.features.newidea.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.R

class IdeaFragmentR : Fragment(), IdeaInfoProvider {

    private lateinit var resumoProblemaTextView: TextView
    private lateinit var resumoSolucaoTextView: TextView
    private lateinit var etTituloEditText: EditText

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

        val problema = arguments?.getString("problema") ?: "Nenhum problema informado"
        val solucao = arguments?.getString("solucao") ?: "Nenhuma solucao informada"

        resumoProblemaTextView.text = problema
        resumoSolucaoTextView.text = solucao

        val tituloPreenchido = arguments?.getString("titulo_ideia")
        if (!tituloPreenchido.isNullOrEmpty()) {
            etTituloEditText.setText(tituloPreenchido)
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