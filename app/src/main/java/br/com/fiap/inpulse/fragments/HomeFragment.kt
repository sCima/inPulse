package br.com.fiap.inpulse.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Idea
import br.com.fiap.inpulse.viewmodel.IdeaAdapter

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IdeaAdapter

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

        val ideias = mutableListOf(
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

        adapter = IdeaAdapter(ideias)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

}