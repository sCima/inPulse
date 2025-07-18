package br.com.fiap.inpulse.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.response.Contribuicao

class ContAdapter(private var conts: MutableList<Contribuicao>) :
    RecyclerView.Adapter<ContAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<TextView>(R.id.textContUser)
        val cont: TextView = itemView.findViewById(R.id.textCont)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_conts, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val conts = conts[position]
        holder.nome.text = conts.nomeAutor
        holder.cont.text = conts.coment
    }

    override fun getItemCount(): Int = conts.size

    fun addItemAtTop(cont: Contribuicao) {
        conts.add(0, cont)
        notifyItemInserted(0)
    }
}