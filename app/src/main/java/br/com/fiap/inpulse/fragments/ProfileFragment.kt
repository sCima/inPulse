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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Idea
import br.com.fiap.inpulse.model.Selo
import br.com.fiap.inpulse.viewmodel.IdeaProfileAdapter
import br.com.fiap.inpulse.viewmodel.SeloAdapter

class ProfileFragment : Fragment() {

    private lateinit var recyclerViewC: RecyclerView
    private lateinit var recyclerViewI: RecyclerView
    private lateinit var recyclerViewS: RecyclerView
    private lateinit var adapter: IdeaProfileAdapter
    private lateinit var adapterS: SeloAdapter

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
        val recyclerViewS = view.findViewById<RecyclerView>(R.id.recyclerViewProfileSelos)
        val containerStats = view.findViewById<View>(R.id.containerStats)
        val fgBar = resources.getColor(R. color. fgBar)

        val background: View = view.findViewById(R.id.profile_fragment)
        background.setBackgroundColor(getResources().getColor(R.color.bronze))

        btnIdeas.setOnClickListener {
            recyclerViewI.visibility = View.VISIBLE
            recyclerViewC.visibility = View.GONE
            containerStats.visibility = View.GONE
            btnIdeas.setBackgroundColor(fgBar)
            btnCont.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnCont.setOnClickListener {
            recyclerViewC.visibility = View.VISIBLE
            recyclerViewI.visibility = View.GONE
            containerStats.visibility = View.GONE
            btnCont.setBackgroundColor(fgBar)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnStats.setBackgroundColor(Color.TRANSPARENT)
        }
        btnStats.setOnClickListener {
            recyclerViewC.visibility = View.GONE
            recyclerViewI.visibility = View.GONE
            containerStats.visibility = View.VISIBLE
            btnStats.setBackgroundColor(fgBar)
            btnIdeas.setBackgroundColor(Color.TRANSPARENT)
            btnCont.setBackgroundColor(Color.TRANSPARENT)
        }

        val ideas = mutableListOf(
            Idea("Plataforma de estudos gamificada",
                "Lorem ipsum dolor sit amet."),

            Idea("Rede social para pets",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque vel neque nec nulla scelerisque efficitur."),

            Idea("Organizador de tarefas por voz",
                "Lorem ipsum dolor sit amet, consectetur."),

            Idea("App para dividir contas de casa",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam in tortor ut est fringilla sollicitudin."),

            Idea("Monitor de produtividade em tempo real",
                "Lorem ipsum."),

            Idea("Consultoria de moda com IA",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. Morbi eu nisl nec velit fermentum rutrum."),

            Idea("Detector de mentiras por texto",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque facilisis, ex sed sagittis commodo, velit velit efficitur elit, eget tincidunt sapien nulla nec risus.")

        )
        val conts = mutableListOf(
            Idea("Aplicativo para lembrar de beber água",
                "Muitas pessoas esquecem de se hidratar ao longo do dia"),

            Idea("Sistema de filas com QR Code",
                "Longas filas em estabelecimentos causam perda de tempo e frustração"),

            Idea("Plataforma para trocar serviços entre vizinhos",
                "Falta de acesso fácil a pequenos serviços em comunidades locais"),

            Idea("App para treinar soft skills com IA",
                "Muitos profissionais têm dificuldade em desenvolver habilidades interpessoais"),

            Idea("Mapa colaborativo de lugares acessíveis",
                "Pessoas com deficiência enfrentam obstáculos ao se locomover em locais públicos")
        )

        val selos = mutableListOf(
            Selo("Java Ninja"),
            Selo("Kotlin Pro"),
            Selo("SQL Mestre"),
            Selo("UX Expert"),
            Selo("Bug Slayer"),
            Selo("Code Hero"),
            Selo("Debug King"),
            Selo("Front Star"),
            Selo("Test Guru"),
            Selo("Clean Code"),
            Selo("App Maker"),
            Selo("DB Master"),
            Selo("API Lord")
        )

        adapter = IdeaProfileAdapter(ideas)
        recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewI.adapter = adapter

        adapter = IdeaProfileAdapter(conts)
        recyclerViewC.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewC.adapter = adapter

        adapterS = SeloAdapter(selos)
        val layoutManagers = GridLayoutManager(requireContext(), 4)
        recyclerViewS.layoutManager = layoutManagers
        recyclerViewS.adapter = adapterS
    }

}