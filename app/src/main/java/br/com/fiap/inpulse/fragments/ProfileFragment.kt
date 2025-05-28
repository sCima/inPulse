package br.com.fiap.inpulse.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Idea
import br.com.fiap.inpulse.viewmodel.IdeaProfileAdapter

class ProfileFragment : Fragment() {

    private lateinit var recyclerViewC: RecyclerView
    private lateinit var recyclerViewI: RecyclerView
    private lateinit var adapter: IdeaProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnIdeas: TextView = view.findViewById(R.id.btn_ideias_profile)
        val btnCont: TextView = view.findViewById(R.id.btn_contribuicoes_profile)
        val btnStats: TextView = view.findViewById(R.id.btnStatsProfile)
        val recyclerViewC = view.findViewById<RecyclerView>(R.id.recyclerViewProfileContribuicoes)
        val recyclerViewI = view.findViewById<RecyclerView>(R.id.recyclerViewProfileIdeas)
        val fgBar = resources.getColor(R. color. fgBar)

        val background: View = view.findViewById(R.id.profile_fragment)
        background.setBackgroundColor(getResources().getColor(R.color.bronze))
        
        btnIdeas.setOnClickListener {
            recyclerViewI.visibility = View.VISIBLE
            recyclerViewC.visibility = View.GONE
            btnIdeas.setBackgroundColor(fgBar)
            btnCont.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnCont.setOnClickListener {
            recyclerViewC.visibility = View.VISIBLE
            recyclerViewI.visibility = View.GONE
            btnCont.setBackgroundColor(fgBar)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnStats.setOnClickListener {
            recyclerViewC.visibility = View.GONE
            recyclerViewI.visibility = View.GONE
            btnStats.setBackgroundColor(fgBar)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnCont.setBackgroundColor(Color.TRANSPARENT)
        }

        val ideas = mutableListOf(
            Idea("Ideia sobre sei lá oq",
                "Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat."),
            Idea("Usar chatgpt pra criar remédios",
                "Lorem ipsum dolor Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat.. Quisque faucibus ex sapien vitae pellentesque sem placerat."),
            Idea("Ideia sobre sim",
                "Lorem ipsum et dolor consectetur adipiscing elit.")
        )
        val conts = mutableListOf(
            Idea("Ideia sobre bla bla",
                "Teste teste"),
            Idea("Usar chatgpt pra tudo",
                "Lorem ipsum et dolor dolor"),
            Idea("Ideia sobre nao",
                "Lorem ipsum et dolor dolor")
        )

        adapter = IdeaProfileAdapter(ideas)
        recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewI.adapter = adapter

        adapter = IdeaProfileAdapter(conts)
        recyclerViewC.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewC.adapter = adapter
    }

}