package br.com.fiap.inpulse.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            Idea("Ideia sobre logistica",
                "Lorem ipsum et dolor bla bla vai corinthias texto texto aloalo",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur nec nisi in sapien dignissim dictum. Integer blandit, magna ut tincidunt feugiat, sem erat rhoncus tellus, in sodales sapien nisi at nisl. Proin tincidunt arcu in justo sodales, sed commodo sapien cursus. Pellentesque a elit at eros sodales posuere. Ut suscipit lacinia justo, vel luctus leo cursus nec.",
                "21/05/2025",
                "Zézinho"),
            Idea("Automação de segurança",
                "Problema nas máquinas agrícolas",
                "Pellentesque a elit at eros sodales posuere. Ut suscipit lacinia justo, vel luctus leo cursus nec.",
                "22/05/2025",
                "Rodolfo Lanches"),
            Idea("Automação de segurança",
                "Problema nas máquinas agrícolas",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur nec nisi in sapien dignissim dictum. Integer blandit, magna ut tincidunt feugiat, sem erat rhoncus tellus, in sodales sapien nisi at nisl. Proin tincidunt arcu in justo sodales, sed commodo sapien cursus. Pellentesque a elit at eros sodales posuere. Ut suscipit lacinia justo, vel luctus leo cursus nec.",
                "22/05/2025",
                "Rodolfo Lanches")
        )

        adapter = IdeaAdapter(ideias)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

}