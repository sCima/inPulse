package br.com.fiap.inpulse.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.model.Cont
import br.com.fiap.inpulse.model.Idea

class IdeaAdapter(private var ideas: MutableList<Idea>) :
    RecyclerView.Adapter<IdeaAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<View>(R.id.includeBar)
            .findViewById(R.id.fragment_bar_title)
        val problema: TextView = itemView.findViewById(R.id.resumoIdeia)
        val descricao: TextView = itemView.findViewById(R.id.textoCompleto)
        val data: TextView = itemView.findViewById(R.id.dataIdeia)
        val autor: TextView = itemView.findViewById(R.id.autorIdeia)
        val likes: TextView = itemView.findViewById(R.id.numberLikes)
        val btnCurtir: ImageButton = itemView.findViewById(R.id.btnCurtir)

        val btnConts: ImageButton = itemView.findViewById(R.id.btnContribuir)
        val btnVoltar: ImageButton = itemView.findViewById(R.id.btnVoltarItem)
        val layoutContsFoot: View = itemView.findViewById(R.id.footerConts)
        val containerInterno: View = itemView.findViewById(R.id.containerInterno)
        val recyclerConts: RecyclerView = itemView.findViewById(R.id.recyclerConts)

        var liked = false
        private var contsVisible = false

        fun bind(idea: Idea) {
            nome.text = idea.nome
            problema.text = idea.problema
            descricao.text = idea.descricao
            data.text = idea.data
            autor.text = idea.autor
            likes.text = idea.likes.toString()

            btnCurtir.setImageResource(
                if (liked) R.drawable.baseline_liked_24 else R.drawable.baseline_like_24
            )

            btnCurtir.setOnClickListener {
                var count = likes.text.toString().toIntOrNull() ?: 0
                liked = !liked
                count = if (liked) count + 1 else count - 1

                btnCurtir.setImageResource(
                    if (liked) R.drawable.baseline_liked_24 else R.drawable.baseline_like_24
                )
                likes.text = itemView.context.getString(R.string.number_likes, count)
            }

            containerInterno.visibility = if (contsVisible) View.GONE else View.VISIBLE
            recyclerConts.visibility = if (contsVisible) View.VISIBLE else View.GONE
            layoutContsFoot.visibility = if (contsVisible) View.VISIBLE else View.GONE
            btnVoltar.visibility = if (contsVisible) View.VISIBLE else View.GONE


            val conts = mutableListOf(
                Cont("Ricardinho",
                    "Teste teste"),
                Cont("Souza",
                    "Lorem ipsum et dolor dolor"),
                Cont("Felipe Cortez",
                    "Lorem ipsum et dolor dolor")
            )

            val adapter = ContAdapter(conts)
            recyclerConts.layoutManager = LinearLayoutManager(itemView.context)
            recyclerConts.adapter = adapter

            btnConts.setOnClickListener {
                contsVisible = !contsVisible
                containerInterno.fadeOut()
                recyclerConts.fadeIn()
                layoutContsFoot.fadeIn()
                btnVoltar.fadeIn()
            }

            btnVoltar.setOnClickListener {
                contsVisible = false
                recyclerConts.fadeOut()
                layoutContsFoot.fadeOut()
                btnVoltar.fadeOut()
                containerInterno.fadeIn()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_ideias, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(ideas[position])
    }

    override fun getItemCount(): Int = ideas.size

    fun addItemAtTop(idea: Idea) {
        ideas.add(0, idea)
        notifyItemInserted(0)
    }

}

    private fun View.fadeIn(duration: Long = 300) {
        this.alpha = 0f
        this.visibility = View.VISIBLE
        this.animate().alpha(1f).setDuration(duration).start()
    }

    private fun View.fadeOut(duration: Long = 300) {
        this.animate().alpha(0f).setDuration(duration).withEndAction {
            this.visibility = View.GONE
        }.start()
    }
