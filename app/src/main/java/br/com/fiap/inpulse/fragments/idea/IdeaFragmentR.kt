package br.com.fiap.inpulse.fragments.idea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.R

class IdeaFragmentR : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideia_r, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val problema = arguments?.getString("problema") ?: "Nenhum problema informado"
        val solucao = arguments?.getString("solucao") ?: "Nenhuma solucao informada"

        val resumoProblema = view.findViewById<TextView>(R.id.txt_resumo_problema)
        resumoProblema.text = problema

        val resumoSolucao = view.findViewById<TextView>(R.id.txt_resumo_solucao)
        resumoSolucao.text = solucao

    }
}