    package br.com.fiap.inpulse.viewmodel

    import android.util.TypedValue
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageButton
    import android.widget.TextView
    import androidx.core.content.ContentProviderCompat.requireContext
    import androidx.recyclerview.widget.GridLayoutManager
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
                .findViewById(R.id.fragment_bar_title)
            val dataInicio: TextView = itemView.findViewById(R.id.dataInicioPrograma)
            val dataFim: TextView = itemView.findViewById(R.id.dataFimPrograma)
            val desc: TextView = itemView.findViewById(R.id.descricaoPrograma)
            val btnEnviarIdeia: TextView = itemView.findViewById(R.id.btnEnviarIdeia)
            val recyclerIdeasP: RecyclerView = itemView.findViewById(R.id.recyclerViewIdeasPrograma)

            fun bind(programa: Programa) {
                nome.text = programa.nome
                dataInicio.text = programa.dataInicio
                dataFim.text = itemView.context.getString(R.string.ate_data, programa.dataInicio)
                desc.text = programa.desc

                val ideias = mutableListOf(
                    Idea("Ideia X",
                        "Felipe C."),
                    Idea("Ideia Y",
                        "Lucas de L."),
                    Idea("Ideia Z",
                        "Guilherme B.") ,
                    Idea("Ideia A",
                    "Rodolfo S."),
                    Idea("Ideia B",
                    "Carlos S.")
                )

                val adapter = IdeaProgramaAdapter(ideias)
                recyclerIdeasP.layoutManager = GridLayoutManager(itemView.context, 2)
                recyclerIdeasP.adapter = adapter
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