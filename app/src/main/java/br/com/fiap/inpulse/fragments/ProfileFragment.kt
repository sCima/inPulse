package br.com.fiap.inpulse.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.response.FuncionarioResponse
import br.com.fiap.inpulse.data.response.IdeiaResponse
import br.com.fiap.inpulse.model.Selo
import br.com.fiap.inpulse.viewmodel.IdeaAdapter
import br.com.fiap.inpulse.viewmodel.IdeaProfileAdapter
import br.com.fiap.inpulse.viewmodel.SeloAdapter

class ProfileFragment : Fragment() {

    private lateinit var adapter: IdeaAdapter
    private lateinit var adapterS: SeloAdapter
    private lateinit var adapterC: IdeaProfileAdapter
    private var funcionarioData: FuncionarioResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            funcionarioData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("funcionario_profile_data", FuncionarioResponse::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable("funcionario_profile_data")
            }
        }

        val btnIdeas: TextView = view.findViewById(R.id.btn_ideias_profile)
        val btnProg: TextView = view.findViewById(R.id.btn_programas_profile)
        val btnStats: TextView = view.findViewById(R.id.btnStatsProfile)
        val recyclerViewC = view.findViewById<RecyclerView>(R.id.recyclerViewProfileContribuicoes)
        val recyclerViewI = view.findViewById<RecyclerView>(R.id.recyclerViewProfileIdeas)
        val recyclerViewS = view.findViewById<RecyclerView>(R.id.recyclerViewProfileSelos)
        val containerStats = view.findViewById<View>(R.id.containerStats)
        val fgBar = resources.getColor(R. color. fgBar)

        val tvProximoNivel = view.findViewById<TextView>(R.id.next_tier)
        tvProximoNivel.text = (when (funcionarioData?.tier) {
            "Bronze" -> {
                "Próximo nível: Prata"
            }
            "Prata" -> {
                "Próximo nível: Ouro"
            }
            else -> {
                "-"
            }
        })

        val tvNumeroIdeias = view.findViewById<TextView>(R.id.text_stat_ideias)
        val numero = funcionarioData?.ideias?.size.toString()
        tvNumeroIdeias.text = "Total de ideias: $numero"

        val tvNumeroLikes = view.findViewById<TextView>(R.id.text_stat_likes)
        val soma = funcionarioData?.ideias?.sumOf{it.curtidas}
        tvNumeroLikes.text = "Curtidas: ${soma.toString()}"

        val tvNumeroConts = view.findViewById<TextView>(R.id.text_stat_conts)
        tvNumeroConts.text = "Contribuições: 0"

        val background: View = view.findViewById(R.id.profile_fragment)
        background.setBackgroundColor(getResources().getColor(R.color.bronze))

        btnIdeas.setOnClickListener {
            recyclerViewI.visibility = View.VISIBLE
            recyclerViewC.visibility = View.GONE
            containerStats.visibility = View.GONE
            btnIdeas.setBackgroundColor(fgBar)
            btnProg.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnProg.setOnClickListener {
            recyclerViewC.visibility = View.VISIBLE
            recyclerViewI.visibility = View.GONE
            containerStats.visibility = View.GONE
            btnProg.setBackgroundColor(fgBar)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnStats.setOnClickListener {
            recyclerViewC.visibility = View.GONE
            recyclerViewI.visibility = View.GONE
            containerStats.visibility = View.VISIBLE
            btnStats.setBackgroundColor(fgBar)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnProg.setBackgroundColor(Color.TRANSPARENT)
        }

        funcionarioData?.let { funcionario ->

            val ideiasConvertidas: MutableList<IdeiaResponse> = funcionario.ideias.map { ideiaFuncionario ->
                IdeiaResponse(
                    ideiaFuncionario = ideiaFuncionario,
                    funcionarioNome = "${funcionario.primeiro_nome} ${funcionario.ultimo_sobrenome}",
                    contribuicoesPadrao = emptyList()
                )
            }.toMutableList()

            adapter = IdeaAdapter(ideiasConvertidas, "ProfileFragment")
            recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewI.adapter = adapter
            recyclerViewI.visibility = View.VISIBLE

            val selosDoFuncionario: MutableList<Selo> = funcionario.selos.map { Selo(it) }.toMutableList()
            adapterS = SeloAdapter(selosDoFuncionario)
            val layoutManagers = GridLayoutManager(requireContext(), 4)
            recyclerViewS.layoutManager = layoutManagers
            recyclerViewS.adapter = adapterS
            recyclerViewS.visibility = View.GONE

        } ?: run {
            adapter = IdeaAdapter(mutableListOf(), "ProfileFragment")
            recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewI.adapter = adapter
            recyclerViewI.visibility = View.VISIBLE

            adapterC = IdeaProfileAdapter(mutableListOf())
            recyclerViewC.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewC.adapter = adapterC
            recyclerViewC.visibility = View.GONE

            adapterS = SeloAdapter(mutableListOf())
            val layoutManagers = GridLayoutManager(requireContext(), 4)
            recyclerViewS.layoutManager = layoutManagers
            recyclerViewS.adapter = adapterS
            recyclerViewS.visibility = View.GONE

        }
    }

}
