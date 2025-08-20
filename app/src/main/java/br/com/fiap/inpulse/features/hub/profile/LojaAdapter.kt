package br.com.fiap.inpulse.features.hub.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.ItemLoja
import br.com.fiap.inpulse.data.model.Selo

class LojaAdapter(private var itens: MutableList<ItemLoja>) :
    RecyclerView.Adapter<LojaAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = itemView.findViewById<TextView>(R.id.nome_item)
        val img: ImageView = itemView.findViewById(R.id.img_item)
        val preco = itemView.findViewById<TextView>(R.id.preco_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_loja, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val itens = itens[position]
        holder.nome.text = itens.nome
        holder.preco.text = itens.preco
    }

    override fun getItemCount(): Int = itens.size
}