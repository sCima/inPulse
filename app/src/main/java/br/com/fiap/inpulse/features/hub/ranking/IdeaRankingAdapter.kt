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
        val pos: TextView = itemView.findViewById(R.id.ranking_idea_pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_ranking_ideas, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val idea = ideas[position]
        holder.nome.text = idea.nome
        holder.numero.text = idea.valorOrdenacao
        holder.pos.text = "#${position + 1}"
    }

    override fun getItemCount(): Int = ideas.size

    fun updateAndSortIdeas(newIdeas: List<IdeiaResponse>, sortBy: String) {
        this.ideas.clear()
        this.ideas.addAll(newIdeas)

        when (sortBy.lowercase()) {
            "contribuições" -> {
                this.ideas.sortByDescending { it.contribuicoes.size }
                ideas.forEach { it.valorOrdenacao = it.contribuicoes.size.toString() }
            }
            "programas" -> {
                this.ideas.sortByDescending { it.programas_nome.size }
                ideas.forEach { it.valorOrdenacao = it.programas_nome.size.toString() }
            }
            else -> {
                this.ideas.sortByDescending { it.curtidas }
                ideas.forEach { it.valorOrdenacao = it.curtidas.toString() }
            }
        }
        notifyDataSetChanged()
    }
}