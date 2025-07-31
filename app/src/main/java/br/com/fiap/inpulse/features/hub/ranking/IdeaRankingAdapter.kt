package br.com.fiap.inpulse.features.hub.ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.IdeiaResponse

class IdeaRankingAdapter(var ideas: MutableList<IdeiaResponse>) :
    RecyclerView.Adapter<IdeaRankingAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.textIdeaName)
        val numero: TextView = itemView.findViewById(R.id.textIdeaNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_ranking_ideas, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val idea = ideas[position]
        holder.nome.text = idea.nome
        holder.numero.text = idea.curtidas.toString()

    }

    override fun getItemCount(): Int = ideas.size

    fun updateAndSortIdeas(newIdeas: List<IdeiaResponse>) {
        this.ideas.clear()
        this.ideas.addAll(newIdeas)
        this.ideas.sortByDescending { it.curtidas }

        notifyDataSetChanged()
    }
}