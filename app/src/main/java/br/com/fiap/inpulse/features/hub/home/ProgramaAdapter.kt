    package br.com.fiap.inpulse.features.hub.home

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import br.com.fiap.inpulse.R
    import br.com.fiap.inpulse.data.model.response.ProgramaResponse
    import java.text.SimpleDateFormat
    import java.util.Locale

    class ProgramaAdapter(var programas: MutableList<ProgramaResponse>) :
        RecyclerView.Adapter<ProgramaAdapter.InfoViewHolder>() {

        class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nome: TextView = itemView.findViewById<View>(R.id.includeBarP)
                .findViewById(R.id.fragment_bar_title)
            val dataInicio: TextView = itemView.findViewById(R.id.dataInicioPrograma)
            val dataFim: TextView = itemView.findViewById(R.id.dataFimPrograma)
            val desc: TextView = itemView.findViewById(R.id.descricaoPrograma)
            val btnEnviarIdeia: TextView = itemView.findViewById(R.id.btnEnviarIdeia)
            val recyclerIdeasP: RecyclerView = itemView.findViewById(R.id.recyclerViewIdeasPrograma)

            fun bind(programa: ProgramaResponse) {
                nome.text = programa.nome_programa
                val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                try {
                    val date = inputFormat.parse(programa.dataInicio)
                    if (date != null) {
                        dataInicio.text = outputFormat.format(date)
                    } else {
                        dataInicio.text = "Data inválida"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    dataInicio.text = "Erro na data"
                }

                try {
                    val dateF = inputFormat.parse(programa.dataFim)
                    if (dateF != null) {
                        dataFim.text = itemView.context.getString(R.string.ate_data, outputFormat.format(dateF))
                    } else {
                        dataFim.text = "Data inválida"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    dataFim.text = "Erro na data"
                }
                desc.text = programa.descricao_programa
                val ideias = programa.funcionarios_nome
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

        fun addItemAtTop(programa: ProgramaResponse) {
            programas.add(0, programa)
            notifyItemInserted(0)
        }
    }