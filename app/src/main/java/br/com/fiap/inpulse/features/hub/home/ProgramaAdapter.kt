package br.com.fiap.inpulse.features.hub.home

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.data.model.response.IdeiaResponse
import br.com.fiap.inpulse.data.model.response.ProgramaResponse
import java.text.SimpleDateFormat
import java.util.Locale

class ProgramaAdapter(var programas: MutableList<ProgramaResponse>,
                      private val ideiasR:  MutableList<IdeiaResponse>,
                      private val fragment: String,
                      private val lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<ProgramaAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View,
                         private val ideiasR:  MutableList<IdeiaResponse>,
                         private val fragment: String,
                         private val lifecycleOwner: LifecycleOwner
        ) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<View>(R.id.includeBarP)
            .findViewById(R.id.fragment_bar_title)
        val dataInicio: TextView = itemView.findViewById(R.id.dataInicioPrograma)
        val dataFim: TextView = itemView.findViewById(R.id.dataFimPrograma)
        val desc: TextView = itemView.findViewById(R.id.descricaoPrograma)
        val btnEnviarIdeia: ImageView = itemView.findViewById(R.id.btnEnviarIdeia)
        val btnInscrever: Button = itemView.findViewById(R.id.btnInscrever)
        val recyclerIdeasP: RecyclerView = itemView.findViewById(R.id.recyclerViewIdeasPrograma)
        val recyclerIdeasS: RecyclerView = itemView.findViewById(R.id.recyclerViewSuasIdeasPrograma)
        val containerInternoP: ConstraintLayout = itemView.findViewById(R.id.containerInternoP)
        var layoutCompleto: View = itemView.findViewById(R.id.layout_programas)
        val barraTitulo: ConstraintLayout = itemView.findViewById(R.id.includeBarP)

        private var subscribed = false

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

            updateEnviarIdeiaButtonState()

//            btnInscrever.setOnClickListener {
//                lifecycleOwner.lifecycleScope.launch {
//                    withContext(Dispatchers.IO) {
//                        try {
//                            val requestBody =
//                                //RetrofitClient.inPulseApiService.sendInscricao()
//                                withContext(Dispatchers.Main) {
//                                    subscribed = true
//                                    btnInscrever.text = "Inscrito"
//                                    updateEnviarIdeiaButtonState()
//                                }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(
//                                    itemView.context,
//                                    "Erro: ${e.message}",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                e.printStackTrace()
//                                subscribed = false
//                                btnInscrever.text = "Inscrever-se"
//                                updateEnviarIdeiaButtonState()
//                            }
//                        }
//                    }
//                }
//            }

            if(ideiasR.isNotEmpty()) {
                val adapterS = SuasIdeiasAdapter(ideiasR)
                recyclerIdeasS.layoutManager = GridLayoutManager(itemView.context, 2)
                recyclerIdeasS.adapter = adapterS
            } else {
                val adapterS = SuasIdeiasAdapter(mutableListOf())
                recyclerIdeasS.layoutManager = GridLayoutManager(itemView.context, 2)
                recyclerIdeasS.adapter = adapterS
            }

            btnEnviarIdeia.setOnClickListener {
                if (subscribed) {
                    recyclerIdeasP.visibility = View.GONE
                    recyclerIdeasS.visibility = View.VISIBLE
                } else {
                    Toast.makeText(itemView.context, "Você precisa se inscrever no programa primeiro.", Toast.LENGTH_SHORT).show()
                }
            }

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

        private fun updateEnviarIdeiaButtonState() {
            if (subscribed) {
                btnEnviarIdeia.setImageResource(R.drawable.baseline_enviar_ideias_24)
                btnEnviarIdeia.isEnabled = true
                btnEnviarIdeia.isClickable = true
            } else {
                btnEnviarIdeia.setImageResource(R.drawable.baseline_enviar_ideias_bloq_24)
                btnEnviarIdeia.isEnabled = false
                btnEnviarIdeia.isClickable = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_programas, parent, false)
        return InfoViewHolder(view, ideiasR, fragment, lifecycleOwner)
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