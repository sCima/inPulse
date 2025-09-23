package br.com.fiap.inpulse.features.hub.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.data.model.response.ItemResponse
import com.google.gson.Gson

interface OnItemPurchaseListener {
    fun onItemPurchased(novoTotalMoedas: Int)
}

class LojaAdapter(private var itens: MutableList<ItemResponse>,
                  private var tier: String?,
                  private val purchaseListener: OnItemPurchaseListener) :
    RecyclerView.Adapter<LojaAdapter.InfoViewHolder>() {

    // --- Constantes para SharedPreferences ---
    private val PREFS_NAME = "InPulsePrefs"
    private val KEY_FUNCIONARIO_JSON = "funcionario_json"

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
        val context = holder.itemView.context

        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val funcionarioJson = sharedPref.getString(KEY_FUNCIONARIO_JSON, null)

        if (funcionarioJson == null) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            return
        }

        val loggedUser = Gson().fromJson(funcionarioJson, FuncionarioResponse::class.java)
        val loggedUserId = loggedUser.funcionario_id
        var userMoedas = loggedUser.moedas

        val ownerIds = item.funcionarios.map { it }.toSet()
        val isOwned = ownerIds.contains(loggedUserId)

        TooltipCompat.setTooltipText(holder.itemView, item.descricao)

        if (isOwned) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            return
        } else {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        holder.nome.text = item.nome
        holder.preco.text = "${item.preco} EC"

        val itemDesbloqueado = isItemDesbloqueado(item.tier, this.tier)

        if (itemDesbloqueado) {
            holder.img.setImageResource(R.drawable.item_loja)
        } else {
            holder.img.setImageResource(R.drawable.item_loja_bloq)
        }

        holder.itemView.setOnClickListener {
            if (!itemDesbloqueado) {
                Toast.makeText(context, "Item bloqueado! Você precisa atingir o tier ${item.tier}.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userMoedas >= item.preco) {
                val novoTotalMoedas = userMoedas - item.preco

                Toast.makeText(context, "'${item.nome}' comprado com sucesso!", Toast.LENGTH_SHORT).show()

                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    itens.removeAt(currentPosition)
                    notifyItemRemoved(currentPosition)
                }

                val updatedFuncionario = loggedUser.copy(moedas = novoTotalMoedas)
                val updatedJson = Gson().toJson(updatedFuncionario)
                sharedPref.edit().putString(KEY_FUNCIONARIO_JSON, updatedJson).apply()

                purchaseListener.onItemPurchased(novoTotalMoedas)
                // TODO - call api e descontar moedas

            } else {
                Toast.makeText(context, "Moedas insuficientes. Você precisa de ${item.preco} moedas.", Toast.LENGTH_SHORT).show()
            }
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