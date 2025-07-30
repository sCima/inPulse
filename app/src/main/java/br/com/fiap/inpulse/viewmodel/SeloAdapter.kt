package br.com.fiap.inpulse.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Selo

class SeloAdapter(private var selos: MutableList<Selo>) :
    RecyclerView.Adapter<SeloAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<TextView>(R.id.nome_selo)
        val img: ImageView = itemView.findViewById(R.id.img_selo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_selos, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val selos = selos[position]
        holder.nome.text = selos.nome
    }

    override fun getItemCount(): Int = selos.size

    fun addItemAtTop(selo: Selo) {
        selos.add(0, selo)
        notifyItemInserted(0)
    }
}