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
import br.com.fiap.inpulse.model.Programa

class ProgramaAdapter(private var programas: MutableList<Programa>) :
    RecyclerView.Adapter<ProgramaAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<View>(R.id.includeBarP)
            .findViewById(R.id.fragment_bar_title_p)
        val dataInicio: TextView = itemView.findViewById(R.id.dataInicioPrograma)
        val dataFim: TextView = itemView.findViewById(R.id.dataFimPrograma)
        val desc: TextView = itemView.findViewById(R.id.descricaoPrograma)
        val btnVerIdeias: TextView = itemView.findViewById(R.id.verIdeias)
        val btnEnviarIdeia: ImageButton = itemView.findViewById(R.id.btnEnviarIdeia)
        val recyclerIdeasP: RecyclerView = itemView.findViewById(R.id.recyclerIdeiasP)
        val containerInternoP: View = itemView.findViewById(R.id.containerInternoP)
        val btnVoltar: ImageButton = itemView.findViewById(R.id.btnVoltarItemP)


        fun bind(programa: Programa) {
            nome.text = programa.nome
            dataInicio.text = programa.dataInicio
            dataFim.text = itemView.context.getString(R.string.ate_data, programa.dataFim)
            desc.text = programa.desc

            var ideasPVisible = false

            containerInternoP.visibility = if (ideasPVisible) View.GONE else View.VISIBLE
            recyclerIdeasP.visibility =  if (ideasPVisible) View.VISIBLE else View.GONE
            btnVoltar.visibility =  if (ideasPVisible) View.VISIBLE else View.GONE

            val ideasP = mutableListOf(
                Idea("Ideia X",
                    "Felipe Cortez"),
                Idea("Ideia Y",
                    "Lucas de Lima"),
                Idea("Ideia Z",
                    "Guilherme Bezerra") ,
                Idea("Ideia A",
                "Rodolfo Sanches"),
                Idea("Ideia B",
                "Carlos Sainz")
            )

            val adapter = IdeaProgramaAdapter(ideasP)
            recyclerIdeasP.layoutManager = LinearLayoutManager(itemView.context)
            recyclerIdeasP.adapter = adapter

            btnVerIdeias.setOnClickListener {
                ideasPVisible = !ideasPVisible
                containerInternoP.fadeOut()
                recyclerIdeasP.fadeIn()
                btnVoltar.fadeIn()
            }
            btnVoltar.setOnClickListener {
                ideasPVisible = !ideasPVisible
                containerInternoP.fadeIn()
                recyclerIdeasP.fadeOut()
                btnVoltar.fadeOut()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_programas, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(programas[position])
    }

    override fun getItemCount(): Int = programas.size

    fun addItemAtTop(programa: Programa) {
        programas.add(0, programa)
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