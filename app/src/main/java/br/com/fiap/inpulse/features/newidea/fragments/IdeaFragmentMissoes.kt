package br.com.fiap.inpulse.features.newidea.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.Missao
import br.com.fiap.inpulse.features.hub.home.IdeaAdapter

class IdeaFragmentMissoes: Fragment() {

    private lateinit var adapter: MissoesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideia_missoes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_missoes)

        adapter = MissoesAdapter(mockMissoes())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun mockMissoes() : MutableList<Missao>{
        return mutableListOf(Missao("5 ideias", "Alcançe um total de 5 ideias", 10),
            Missao("Ajudando o TI", "Envie uma ideia da categoria Administração, TI & Finanças", 4),
            Missao("Brinde", "Compre um item da loja", 2),
            Missao("Número 1", "Alcance o topo do ranking de ideias enviadas", 20))
    }

}