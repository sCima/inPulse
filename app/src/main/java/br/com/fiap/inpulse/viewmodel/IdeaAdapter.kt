package br.com.fiap.inpulse.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Idea

class IdeaAdapter(private var ideas: MutableList<Idea>) :
    RecyclerView.Adapter<IdeaAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<View>(R.id.includeBar)
            .findViewById(R.id.fragment_bar_title)
        val problema: TextView = itemView.findViewById(R.id.resumoIdeia)
        val descricao: TextView = itemView.findViewById(R.id.textoCompleto)
        val data: TextView = itemView.findViewById(R.id.dataIdeia)
        val autor: TextView = itemView.findViewById(R.id.autorIdeia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_ideias, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val idea = ideas[position]
        holder.nome.text = idea.nome
        holder.problema.text = idea.problema
        holder.descricao.text = idea.descricao
        holder.data.text = idea.data
        holder.autor.text = idea.autor
    }

    override fun getItemCount(): Int = ideas.size

    fun addItemAtTop(idea: Idea) {
        ideas.add(0, idea)
        notifyItemInserted(0)
    }
}