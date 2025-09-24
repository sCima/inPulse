package br.com.fiap.inpulse.features.hub.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R

class IdeaProgramaAdapter(private var ideas: List<String>) :
    RecyclerView.Adapter<IdeaProgramaAdapter.InfoViewHolder>() {

    private val uniqueIdeas: List<String> = ideas.distinct()

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.ideaPNome)

        fun bind(ideas: String) {
            nome.text = ideas
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_idea_p, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(uniqueIdeas[position])

    }

    override fun getItemCount(): Int = uniqueIdeas.size
}