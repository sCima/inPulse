package br.com.fiap.inpulse.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.RetrofitClient
import br.com.fiap.inpulse.model.IdeaRanking
import br.com.fiap.inpulse.model.UserRanking
import br.com.fiap.inpulse.viewmodel.IdeaRankingAdapter
import br.com.fiap.inpulse.viewmodel.UserRankingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val btnColaboradores: TextView = view.findViewById(R.id.btnColaboradores)
        val recyclerViewU = view.findViewById<RecyclerView>(R.id.recyclerViewRankingUsers)
        val recyclerViewI = view.findViewById<RecyclerView>(R.id.recyclerViewRankingIdeas)
        var rankingIdeias: Boolean = false

        btnColaboradores.setOnClickListener {
            if (!rankingIdeias) {
                recyclerViewU.visibility = View.VISIBLE
                recyclerViewI.visibility = View.GONE
                btnColaboradores.text = "Colaboradores"
                rankingIdeias =! rankingIdeias
            }
            else {
                recyclerViewI.visibility = View.VISIBLE
                recyclerViewU.visibility = View.GONE
                btnColaboradores.text = "Ideias"
                rankingIdeias =! rankingIdeias
            }
        }

        adapterU = UserRankingAdapter(usersMock())
        recyclerViewU.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewU.adapter = adapterU

        adapterI = IdeaRankingAdapter(mutableListOf())
        recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewI.adapter = adapterI

        loadIdeiasFromApi()
    }

    private fun loadIdeiasFromApi() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val ideias = RetrofitClient.inPulseApiService.loadIdeias()
                    withContext(Dispatchers.Main) {
                        if (ideias.isNotEmpty()) {
                            adapterI.ideas.clear()
                            adapterI.ideas.addAll(ideias)
                            adapterI.notifyDataSetChanged()

                            adapterI.updateAndSortIdeas(ideias)
                        } else {
                            Toast.makeText(context, "Nenhuma ideia encontrada.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Falha ao carregar ideias: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
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
                1),
            UserRanking("Souza",
                "Bronze",
                1),
            UserRanking("Renato",
                "Bronze",
                1),
            UserRanking("Nikolai Gogol",
                "Bronze",
                1),
        )
    }