package br.com.fiap.inpulse.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.IdeaRanking
import br.com.fiap.inpulse.model.UserRanking
import br.com.fiap.inpulse.viewmodel.IdeaRankingAdapter
import br.com.fiap.inpulse.viewmodel.UserRankingAdapter

class RankingFragment : Fragment() {

    private lateinit var adapterU: UserRankingAdapter
    private lateinit var adapterI: IdeaRankingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.spinnerFiltro)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filtros_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val btnIdeas: TextView = view.findViewById(R.id.btnIdeias)
        val btnColaboradores: TextView = view.findViewById(R.id.btnColaboradores)
        val recyclerViewU = view.findViewById<RecyclerView>(R.id.recyclerViewRankingUsers)
        val recyclerViewI = view.findViewById<RecyclerView>(R.id.recyclerViewRankingIdeas)
        val bgWhite = resources.getColor(R. color.bgWhite)

        btnIdeas.setOnClickListener {
            recyclerViewI.visibility = View.VISIBLE
            recyclerViewU.visibility = View.GONE
            btnColaboradores.setBackgroundColor(bgWhite)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
        }
        btnColaboradores.setOnClickListener {
            recyclerViewU.visibility = View.VISIBLE
            recyclerViewI.visibility = View.GONE
            btnIdeas.setBackgroundColor(bgWhite)
            btnColaboradores.setBackgroundColor(Color.TRANSPARENT)
        }

        adapterU = UserRankingAdapter(usersMock())
        recyclerViewU.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewU.adapter = adapterU

        adapterI = IdeaRankingAdapter(ideasMock())
        recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewI.adapter = adapterI
    }
}

    private fun usersMock() : MutableList<UserRanking>{
        return mutableListOf(
            UserRanking("Zé zinho",
                "Prata",
                23),
            UserRanking("Rodolfo Lanches",
                "Prata",
                20),
            UserRanking("Emerson",
                "Bronze",
                1)
        )
    }

    private fun ideasMock() : MutableList<IdeaRanking>{
        return mutableListOf(
            IdeaRanking("Ideia sobre sei lá oq",
                20),
            IdeaRanking("Usar chatgpt pra criar remédios",
                13),
            IdeaRanking("Ideia sobre sim",
                10)
        )
    }