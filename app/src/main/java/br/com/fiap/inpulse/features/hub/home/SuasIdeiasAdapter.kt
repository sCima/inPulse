package br.com.fiap.inpulse.features.hub.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.IdeiaFuncionario
import br.com.fiap.inpulse.data.model.response.IdeiaResponse

class SuasIdeiasAdapter(private var ideas: List<IdeiaResponse>) :
    RecyclerView.Adapter<SuasIdeiasAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.ideaPNome)

        fun bind(ideas: IdeiaResponse) {
            nome.text = ideas.nome
            nome.setTextColor(itemView.resources.getColor(R.color.euroBlue))

            nome.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_idea_p, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(ideas[position])

    }

    override fun getItemCount(): Int = ideas.size
}