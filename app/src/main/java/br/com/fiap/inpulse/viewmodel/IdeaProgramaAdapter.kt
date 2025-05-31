package br.com.fiap.inpulse.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Idea
import br.com.fiap.inpulse.model.IdeaRanking

class IdeaProgramaAdapter(private var ideas: MutableList<Idea>) :
    RecyclerView.Adapter<IdeaProgramaAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.ideaPNome)
        val autor: TextView = itemView.findViewById(R.id.ideiaPAutor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_idea_p, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val idea = ideas[position]
        holder.nome.text = idea.nome
        holder.autor.text = idea.autor

    }

    override fun getItemCount(): Int = ideas.size
}