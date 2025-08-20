package br.com.fiap.inpulse.features.hub.ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse

class UserRankingAdapter(var users: MutableList<FuncionarioResponse>) :
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
        holder.nome.text = user.primeiro_nome
        holder.tier.text = user.tier
        holder.numero.text = user.valorOrdenacao
    }

    override fun getItemCount(): Int = users.size

    fun updateAndSortUsers(newUsers: List<FuncionarioResponse>, sortBy: String) {
        this.users.clear()
        this.users.addAll(newUsers)
        when (sortBy.lowercase()) {
            "ideias" -> {
                this.users.sortByDescending { it.ideias.size }
                users.forEach { it.valorOrdenacao = it.ideias.size.toString() }
            }

            "curtidas" -> {
                this.users.sortByDescending { user -> user.ideias.sumOf { it.curtidas } }
                users.forEach { it -> it.valorOrdenacao = it.ideias.sumOf { it.curtidas }.toString() }
            }

            "pontos" -> {
                this.users.sortByDescending { it.pontos }
                users.forEach { it.valorOrdenacao = it.pontos.toString() }
            }

            "selos" -> {
                this.users.sortByDescending { it.selos.size }
                users.forEach { it.valorOrdenacao = it.selos.size.toString() }
            }

            "programas" -> {
                this.users.sortByDescending { it.programas.size }
                users.forEach { it.valorOrdenacao = it.programas.size.toString() }
            }

            else -> {
                this.users.sortByDescending { it.ideias.size }
                users.forEach { it.valorOrdenacao = it.ideias.size.toString() }
            }
        }

        notifyDataSetChanged()
    }
}