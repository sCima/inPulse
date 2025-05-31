package br.com.fiap.inpulse.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Idea
import br.com.fiap.inpulse.model.Programa
import br.com.fiap.inpulse.viewmodel.IdeaAdapter
import br.com.fiap.inpulse.viewmodel.ProgramaAdapter

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IdeaAdapter
    private lateinit var recyclerViewP: RecyclerView
    private lateinit var adapterP: ProgramaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewHome)
        recyclerView.setHasFixedSize(false)
        adapter = IdeaAdapter(mockIdeas())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)


        recyclerViewP = view.findViewById(R.id.recyclerViewHomeP)
        recyclerViewP.setHasFixedSize(false)
        adapterP = ProgramaAdapter(mockProgramas())
        recyclerViewP.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewP.adapter = adapterP
        recyclerViewP.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        val btnIdeiasProgramas: TextView = view.findViewById(R.id.btn_ideias_programas)
        var programasVisible = false

        recyclerViewP.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        btnIdeiasProgramas.setOnClickListener {
            programasVisible = !programasVisible
            if(!programasVisible){
                recyclerView.visibility = View.GONE
                recyclerViewP.visibility = View.VISIBLE
                recyclerViewP.scheduleLayoutAnimation()
                btnIdeiasProgramas.text = "Programas"
            } else {
                recyclerView.visibility = View.VISIBLE
                recyclerViewP.visibility = View.GONE
                recyclerView.scheduleLayoutAnimation()
                btnIdeiasProgramas.text = "Ideias"
            }
        }

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

private fun mockProgramas() : MutableList<Programa>{
    return mutableListOf(
        Programa(
            "Transformação Digital na Produção",
            "01/08/2025",
            "01/10/2025",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        ),

        Programa(
            "Sustentabilidade Inteligente",
            "15/08/2025",
            "15/10/2025",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed nec justo ac risus facilisis lacinia. Proin convallis nisl in ligula posuere, at tempus justo feugiat.",
        ),

        Programa(
            "Experiência do Cliente 360º",
            "20/08/2025",
            "20/10/2025",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.",
        ),

        Programa(
            "Inovação em Segurança Operacional",
            "25/08/2025",
            "25/10/2025",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi vel sagittis leo. Cras tincidunt sem id orci imperdiet, ut fermentum metus iaculis. Integer sit amet purus a erat pharetra tempor.",
        ),

        Programa(
            "Eficiência Energética e Otimização",
            "05/09/2025",
            "05/11/2025",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce non sapien sed mauris dapibus varius. Suspendisse potenti. Quisque fermentum, urna nec gravida mattis, augue nulla eleifend ex, eget sodales velit nulla vel nulla. Vivamus malesuada, est nec scelerisque fermentum, justo est dapibus nisl, ac porta orci risus vitae lorem.",
        )

    )
}


