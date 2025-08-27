package br.com.fiap.inpulse.features.hub.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.IdeiaResponse

class SuasIdeiasAdapter(
    private var ideas: List<IdeiaResponse>,
    private val onIdeiaClicked: (IdeiaResponse) -> Unit
) : RecyclerView.Adapter<SuasIdeiasAdapter.InfoViewHolder>() {

    private var selectedIdeiaId: Int? = null

    fun updateSelection(ideiaId: Int?) {
        this.selectedIdeiaId = ideiaId
        notifyDataSetChanged()
    }

    fun updateData(newIdeas: List<IdeiaResponse>) {
        this.ideas = newIdeas
        notifyDataSetChanged()
    }

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nome: TextView = itemView.findViewById(R.id.ideaPNome)

        fun bind(
            idea: IdeiaResponse,
            isSelected: Boolean,
            onIdeiaClicked: (IdeiaResponse) -> Unit
        ) {
            nome.text = idea.nome

            if (isSelected) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.btnLighterBlue))
                nome.setTextColor(ContextCompat.getColor(itemView.context, R.color.euroBlue))
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
                nome.setTextColor(ContextCompat.getColor(itemView.context, R.color.euroBlue))
            }

            itemView.setOnClickListener {
                onIdeiaClicked(idea)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_idea_p, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val idea = ideas[position]
        val isSelected = idea.ideia_id == selectedIdeiaId
        holder.bind(idea, isSelected, onIdeiaClicked)
    }

    override fun getItemCount(): Int = ideas.size
}