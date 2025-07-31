package br.com.fiap.inpulse.features.hub.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.IdeiaFuncionario

class IdeaProfileAdapter(private var ideas: MutableList<IdeiaFuncionario>) :
    RecyclerView.Adapter<IdeaProfileAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.textIdeaNameProfile)
        val problema: TextView = itemView.findViewById(R.id.textIdeaDescProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_ideias_profile, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val idea = ideas[position]
        holder.nome.text = idea.nome
        holder.problema.text = idea.problema
    }

    override fun getItemCount(): Int = ideas.size

    fun addItemAtTop(idea: IdeiaFuncionario) {
        ideas.add(0, idea)
        notifyItemInserted(0)
    }
}