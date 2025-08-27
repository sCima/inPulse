package br.com.fiap.inpulse.features.newidea.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.Missao

class MissoesAdapter(private var missoes: MutableList<Missao>) :
    RecyclerView.Adapter<MissoesAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_missao_title)
        val desc: TextView = itemView.findViewById(R.id.text_missao_desc)
        val moedas: TextView = itemView.findViewById(R.id.numberMoedas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_missoes, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val missao = missoes[position]
        holder.titulo.text = missao.titulo
        holder.desc.text = missao.desc
        holder.moedas.text = missao.moedas.toString()

        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up_animation)
        )

    }

    override fun getItemCount(): Int {return minOf(missoes.size, 4)}
}