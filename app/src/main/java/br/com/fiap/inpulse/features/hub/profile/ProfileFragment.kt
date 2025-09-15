package br.com.fiap.inpulse.features.hub.profile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.ItemLoja
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.data.model.response.IdeiaResponse
import br.com.fiap.inpulse.data.model.response.ProgramaFuncionario
import br.com.fiap.inpulse.data.model.response.Selo
import br.com.fiap.inpulse.features.hub.ToolbarController
import br.com.fiap.inpulse.features.hub.home.IdeaAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var adapter: IdeaAdapter
    private lateinit var adapterS: SeloAdapter
    private lateinit var adapterP: ProgramaProfileAdapter
    private var funcionarioData: FuncionarioResponse? = null

    private var toolbarListener: ToolbarController? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ToolbarController) {
            toolbarListener = context
        } else {
            throw RuntimeException("$context must implement ToolbarController")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            if (bundle.containsKey("profile_user_id")) {

                val userId = bundle.getInt("profile_user_id")
                fetchFuncionarioData(userId, view)

            } else if (bundle.containsKey("funcionario_profile_data")) {

                funcionarioData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable("funcionario_profile_data", FuncionarioResponse::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    bundle.getParcelable("funcionario_profile_data")
                }

                populateUi(funcionarioData, view)
            }
        }

    }

    private fun fetchFuncionarioData(id: Int, view: View) {
        lifecycleScope.launch {
            try {
                val funcionarioVisitado = withContext(Dispatchers.IO) {
                    RetrofitClient.inPulseApiService.getFuncionarioById(id)
                }
                populateUi(funcionarioVisitado, view)
            } catch (e: Exception) {
                Toast.makeText(context, "Não foi possível carregar o perfil: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun mockItens(): MutableList<ItemLoja> {
        return mutableListOf(ItemLoja("Item", "0 EC", "Bronze"), ItemLoja("Item", "0 EC", "Bronze"),
            ItemLoja("Item", "0 EC", "Prata"), ItemLoja("Item", "0 EC", "Prata"), ItemLoja("Item", "0 EC", "Ouro"))
    }

    private fun populateUi(data: FuncionarioResponse?, view: View) {

        data?.let {
            toolbarListener?.setToolbarForProfile(it)
        }

        funcionarioData = data
        val btnIdeas: TextView = view.findViewById(R.id.btn_ideias_profile)
        val btnProg: TextView = view.findViewById(R.id.btn_programas_profile)
        val btnStats: TextView = view.findViewById(R.id.btnStatsProfile)
        val recyclerViewC = view.findViewById<RecyclerView>(R.id.recyclerViewProfileContribuicoes)
        val recyclerViewI = view.findViewById<RecyclerView>(R.id.recyclerViewProfileIdeas)
        val recyclerViewS = view.findViewById<RecyclerView>(R.id.recyclerViewProfileSelos)
        val recyclerViewLB = view.findViewById<RecyclerView>(R.id.recycler_view_bronze)
        val recyclerViewLP = view.findViewById<RecyclerView>(R.id.recycler_view_prata)
        val recyclerViewLO = view.findViewById<RecyclerView>(R.id.recycler_view_ouro)
        val containerStats = view.findViewById<View>(R.id.containerStats)
        val perfilContent = view.findViewById<View>(R.id.perfil_content)
        val lojaContent = view.findViewById<View>(R.id.loja_content)
        val switch = view.findViewById<MaterialButtonToggleGroup>(R.id.switch_loja)

        val isMyProfile = arguments?.containsKey("funcionario_profile_data") ?: false

        if (isMyProfile) {
            switch.visibility = View.VISIBLE
        } else {
            switch.visibility = View.GONE
            lojaContent.visibility = View.GONE
        }

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
        tvNumeroIdeias.text = "Ideias: $numero"

        val tvNumeroProgramas = view.findViewById<TextView>(R.id.text_stat_programas)
        val qntd = funcionarioData?.programas?.size.toString()
        tvNumeroProgramas.text = "Programas: $qntd"

        val tvNumeroLikes = view.findViewById<TextView>(R.id.text_stat_likes)
        val soma = funcionarioData?.ideias?.sumOf{it.curtidas}
        tvNumeroLikes.text = "Curtidas: ${soma.toString()}"

        val tvPontos = view.findViewById<TextView>(R.id.text_pontos)
        val moedas = funcionarioData?.moedas
        tvPontos.text =  "$moedas Eurocoins disponíveis"

        val tvNumeroConts = view.findViewById<TextView>(R.id.text_stat_conts)
        tvNumeroConts.text = "Contribuições: 0"

        switch.addOnButtonCheckedListener { toggleGroup, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_perfil -> {
                        perfilContent.visibility = View.VISIBLE
                        lojaContent.visibility = View.GONE
                    }
                    R.id.button_loja -> {
                        perfilContent.visibility = View.GONE
                        lojaContent.visibility = View.VISIBLE
                    }
                }
            }
        }

        val todosOsItens = mockItens()
        val itensBronze = todosOsItens.filter { it.tier == "Bronze" }.toMutableList()
        val itensPrata = todosOsItens.filter { it.tier == "Prata" }.toMutableList()
        val itensOuro = todosOsItens.filter { it.tier == "Ouro" }.toMutableList()
        val userTier = funcionarioData?.tier

        val adapterBronze = LojaAdapter(itensBronze, userTier)
        val adapterPrata = LojaAdapter(itensPrata, userTier)
        val adapterOuro = LojaAdapter(itensOuro, userTier)

        recyclerViewLB.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerViewLB.adapter = adapterBronze

        recyclerViewLP.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerViewLP.adapter = adapterPrata

        recyclerViewLO.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerViewLO.adapter = adapterOuro


        val tierColorResId = when (userTier) {
            "Prata" -> R.color.silver
            "Ouro" -> R.color.gold
            else -> R.color.bronze
        }

        val background: View = view.findViewById(R.id.profile_fragment)
        val resolvedColor = ContextCompat.getColor(requireContext(), tierColorResId)
        background.setBackgroundColor(resolvedColor)

        val proximoNivelColorResId = when (userTier) {
            "Bronze" -> R.color.silver
            "Prata" -> R.color.gold
            "Ouro" -> R.color.gold
            else -> R.color.silver
        }

        val proximoNivelColor = ContextCompat.getColor(requireContext(), proximoNivelColorResId)

        tvProximoNivel.backgroundTintList = ColorStateList.valueOf(proximoNivelColor)

        btnIdeas.setOnClickListener {
            recyclerViewI.visibility = View.VISIBLE
            recyclerViewC.visibility = View.GONE
            containerStats.visibility = View.GONE
            btnIdeas.setBackgroundResource(R.drawable.shape_button_blue)
            btnProg.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnProg.setOnClickListener {
            recyclerViewC.visibility = View.VISIBLE
            recyclerViewI.visibility = View.GONE
            containerStats.visibility = View.GONE
            btnProg.setBackgroundResource(R.drawable.shape_button_blue)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnStats.setOnClickListener {
            recyclerViewC.visibility = View.GONE
            recyclerViewI.visibility = View.GONE
            containerStats.visibility = View.VISIBLE
            perfilContent.visibility = View.VISIBLE
            btnStats.setBackgroundResource(R.drawable.shape_button_blue)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnProg.setBackgroundColor(Color.TRANSPARENT)
        }

        funcionarioData?.let { funcionario ->

            val ideiasConvertidas: MutableList<IdeiaResponse> = funcionario.ideias.map { ideiaFuncionario ->
                IdeiaResponse(
                    ideiaFuncionario = ideiaFuncionario,
                    funcionarioNome = ProgramaFuncionario(funcionario.funcionario_id, "${funcionario.primeiro_nome} ${funcionario.ultimo_sobrenome}"),
                    contribuicoesPadrao = emptyList()
                )
            }.toMutableList()

            adapter = IdeaAdapter(ideiasConvertidas, "ProfileFragment", viewLifecycleOwner, null)
            recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewI.adapter = adapter
            recyclerViewI.visibility = View.VISIBLE

            if(!funcionario.programas.isNullOrEmpty()){
                val programasParticipando = funcionarioData!!.programas
                adapterP = ProgramaProfileAdapter(programasParticipando)
                recyclerViewC.layoutManager = LinearLayoutManager(requireContext())
                recyclerViewC.adapter = adapterP
                recyclerViewC.visibility = View.GONE
            }

            val selosDoFuncionario: MutableList<Selo> = funcionario.selos.toMutableList()
            adapterS = SeloAdapter(selosDoFuncionario)
            val layoutManagers = GridLayoutManager(requireContext(), 4)
            recyclerViewS.layoutManager = layoutManagers
            recyclerViewS.adapter = adapterS

        } ?: run {
            adapter = IdeaAdapter(mutableListOf(), "ProfileFragment", viewLifecycleOwner, null)
            recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewI.adapter = adapter
            recyclerViewI.visibility = View.VISIBLE

            adapterS = SeloAdapter(mutableListOf())
            val layoutManagers = GridLayoutManager(requireContext(), 4)
            recyclerViewS.layoutManager = layoutManagers
            recyclerViewS.adapter = adapterS
            recyclerViewS.visibility = View.GONE

        }
    }

    override fun onDetach() {
        super.onDetach()
        toolbarListener = null
    }

}
