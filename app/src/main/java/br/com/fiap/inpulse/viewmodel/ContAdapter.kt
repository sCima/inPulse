package br.com.fiap.inpulse.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Cont

class ContAdapter(private var conts: MutableList<Cont>) :
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
        holder.nome.text = conts.user
        holder.cont.text = conts.cont
    }

    override fun getItemCount(): Int = conts.size

    fun addItemAtTop(cont: Cont) {
        conts.add(0, cont)
        notifyItemInserted(0)
    }
}