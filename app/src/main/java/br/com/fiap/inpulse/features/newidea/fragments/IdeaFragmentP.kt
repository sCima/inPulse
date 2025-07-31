package br.com.fiap.inpulse.features.newidea.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.R

public class IdeaFragmentP : Fragment(), IdeaInfoProvider {

    private lateinit var etProblema: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_idea_p, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etProblema = view.findViewById(R.id.et_problema_desc)

        val sugestoesIds = listOf(
            R.id.sugestao_problema1,
            R.id.sugestao_problema2,
            R.id.sugestao_problema3,
            R.id.sugestao_problema4,
            R.id.sugestao_problema5,
            R.id.sugestao_problema6
        )
        val sugestoes = sugestoesIds.map { id -> view.findViewById<TextView>(id) }

        sugestoes.forEach { tv ->
            tv.setOnClickListener {
                sugestoes.forEach { it.setTypeface(null, Typeface.NORMAL) }
                tv.setTypeface(null, Typeface.BOLD)
                etProblema.setText(tv.text)
            }
        }
    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        bundle.putString("problema", etProblema.text.toString())
        return bundle
    }
}