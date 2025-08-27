package br.com.fiap.inpulse.features.hub.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.ItemLoja

class LojaAdapter(private var itens: MutableList<ItemLoja>, private var tier: String?) :
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
        val item = itens[position]
        holder.nome.text = item.nome
        holder.preco.text = item.preco

        val itemDesbloqueado = isItemDesbloqueado(item.tier, this.tier)

        if (itemDesbloqueado) {
            holder.img.setImageResource(R.drawable.item_loja)
        } else {
            holder.img.setImageResource(R.drawable.item_loja_bloq)
        }
    }

    private fun isItemDesbloqueado(itemTier: String, userTier: String?): Boolean {
        return when (userTier) {
            "Ouro" -> true
            "Prata" -> itemTier == "Bronze" || itemTier == "Prata"
            "Bronze" -> itemTier == "Bronze"
            else -> false
        }
    }

    override fun getItemCount(): Int = itens.size
}