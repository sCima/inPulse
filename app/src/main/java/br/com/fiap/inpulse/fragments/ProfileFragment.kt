package br.com.fiap.inpulse.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.HubActivity
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Idea
import br.com.fiap.inpulse.model.Selo
import br.com.fiap.inpulse.viewmodel.IdeaAdapter
import br.com.fiap.inpulse.viewmodel.IdeaProfileAdapter
import br.com.fiap.inpulse.viewmodel.SeloAdapter

class ProfileFragment : Fragment() {

    private lateinit var adapter: IdeaAdapter
    private lateinit var adapterS: SeloAdapter
    private lateinit var adapterC: IdeaProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnIdeas: TextView = view.findViewById(R.id.btn_ideias_profile)
        val btnProg: TextView = view.findViewById(R.id.btn_programas_profile)
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

        adapter = IdeaAdapter(mockIdeas(), "ProfileFragment")
        recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewI.adapter = adapter
        recyclerViewI.setHasFixedSize(false)

        adapterC = IdeaProfileAdapter(mockConts())
        recyclerViewC.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewC.adapter = adapterC

        adapterS = SeloAdapter(mockSelos())
        val layoutManagers = GridLayoutManager(requireContext(), 4)
        recyclerViewS.layoutManager = layoutManagers
        recyclerViewS.adapter = adapterS
    }

}

private fun mockIdeas() : MutableList<Idea> {
    return mutableListOf(
        Idea(
            "App de rastreio inteligente",
            "Problemas frequentes com extravios de pacotes",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus non volutpat sapien. In at erat lacus. Suspendisse tincidunt justo a orci fermentum, a gravida metus accumsan.",
            "23/05/2025",
            "Marina Silva", 2
        ),

        Idea(
            "Monitoramento ambiental com drones",
            "Falta de dados atualizados sobre áreas de risco",
            "Proin eu sapien a libero malesuada convallis. Curabitur semper, odio a fermentum pretium, neque turpis tempus odio, vitae volutpat odio nulla in mauris.",
            "24/05/2025",
            "Carlos Torres", 6
        ),

        Idea(
            "Sistema de triagem automatizada",
            "Demora no atendimento em hospitais",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras tempor lorem at tortor ultricies, et dapibus lacus scelerisque. Integer ac tincidunt felis.",
            "25/05/2025",
            "Ana Júlia", 11
        ),

        Idea(
            "Aplicativo de economia de energia",
            "Consumo excessivo em prédios corporativos",
            "Aliquam erat volutpat. Aenean condimentum nec velit eget porttitor. Sed lacinia felis vel erat ullamcorper, vel sollicitudin nunc dignissim.",
            "25/05/2025",
            "João Pedro", 1
        ),

        Idea(
            "Ferramenta de revisão automática de contratos",
            "Erros frequentes em documentos jurídicos",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla facilisi. Vestibulum ac eros tempor, sodales sem at, fermentum metus.",
            "26/05/2025",
            "Beatriz Ramos", 9
        )

    )
}

    private fun mockConts() : MutableList<Idea>{
        return mutableListOf(
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
    }

    private fun mockSelos() : MutableList<Selo>{
    return mutableListOf(
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
}
