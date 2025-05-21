package br.com.fiap.inpulse.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.UserRanking

class UserRankingAdapter(private var users: MutableList<UserRanking>) :
    RecyclerView.Adapter<UserRankingAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.textUserName)
        val tier: TextView = itemView.findViewById(R.id.textTier)
        val numero: TextView = itemView.findViewById(R.id.textNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_ranking_users, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val user = users[position]
        holder.nome.text = user.nome
        holder.tier.text = user.tier
        holder.numero.text = user.numero.toString()

    }

    override fun getItemCount(): Int = users.size
}