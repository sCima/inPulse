package br.com.fiap.inpulse.fragments.idea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        bundle.putString("problema", etProblema.text.toString())
        return bundle
    }
}