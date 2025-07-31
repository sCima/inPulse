    package br.com.fiap.inpulse.features.hub.home

    import android.util.TypedValue
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.constraintlayout.widget.ConstraintLayout
    import androidx.lifecycle.LifecycleOwner
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import br.com.fiap.inpulse.R
    import br.com.fiap.inpulse.data.model.response.ProgramaResponse
    import java.text.SimpleDateFormat
    import java.util.Locale

    class ProgramaAdapter(var programas: MutableList<ProgramaResponse>, private val fragment: String, private val lifecycleOwner: LifecycleOwner) :
        RecyclerView.Adapter<ProgramaAdapter.InfoViewHolder>() {

        class InfoViewHolder(itemView: View,
                             private val fragment: String,
                             private val lifecycleOwner: LifecycleOwner
            ) : RecyclerView.ViewHolder(itemView) {
            val nome: TextView = itemView.findViewById<View>(R.id.includeBarP)
                .findViewById(R.id.fragment_bar_title)
            val dataInicio: TextView = itemView.findViewById(R.id.dataInicioPrograma)
            val dataFim: TextView = itemView.findViewById(R.id.dataFimPrograma)
            val desc: TextView = itemView.findViewById(R.id.descricaoPrograma)
            val btnEnviarIdeia: TextView = itemView.findViewById(R.id.btnEnviarIdeia)
            val recyclerIdeasP: RecyclerView = itemView.findViewById(R.id.recyclerViewIdeasPrograma)
            val containerInternoP: ConstraintLayout = itemView.findViewById(R.id.containerInternoP)
            var layoutCompleto: View = itemView.findViewById(R.id.layout_programas)
            val barraTitulo: ConstraintLayout = itemView.findViewById(R.id.includeBarP)


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

                if (fragment == "ProfileFragment") {
                    containerInternoP.visibility = View.GONE
                    val params = layoutCompleto.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    layoutCompleto.layoutParams = params

                    barraTitulo.setBackgroundResource(R.drawable.shape_fragment_body_rounded_p)

                    barraTitulo.setOnClickListener {
                        if (containerInternoP.visibility == View.GONE) {
                            barraTitulo.setBackgroundResource(R.drawable.shape_fragment_bar)

                            containerInternoP.visibility = View.VISIBLE
                            val params = layoutCompleto.layoutParams
                            val pixels = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                350f,  // valor em dp
                                layoutCompleto.resources.displayMetrics
                            ).toInt()
                            params.height = pixels
                            layoutCompleto.layoutParams = params
                        } else if (containerInternoP.visibility == View.VISIBLE) {
                            containerInternoP.visibility = View.GONE
                            barraTitulo.setBackgroundResource(R.drawable.shape_fragment_body_rounded_p)
                            val params = layoutCompleto.layoutParams
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            layoutCompleto.layoutParams = params
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_programas, parent, false)
            return InfoViewHolder(view, fragment, lifecycleOwner)
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