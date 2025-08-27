package br.com.fiap.inpulse.features.newidea.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.R

class IdeaFragmentDescricao : Fragment(), IdeaInfoProvider {

    private lateinit var etSolucao: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideia_descricao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSolucao = view.findViewById(R.id.et_ideia_desc)
    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        bundle.putString("solucao", etSolucao.text.toString())
        return bundle
    }

}