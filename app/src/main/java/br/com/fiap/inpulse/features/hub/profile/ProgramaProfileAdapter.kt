package br.com.fiap.inpulse.features.hub.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.IdeiaFuncionario

class ProgramaProfileAdapter(private var programas: List<String>) :
    RecyclerView.Adapter<ProgramaProfileAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.textIdeaNameProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_ideias_profile, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val programa = programas[position]
        holder.nome.text = programa
    }

    override fun getItemCount(): Int = programas.size
}