package br.com.fiap.inpulse.features.hub.home

import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.request.FuncionarioIdRequest
import br.com.fiap.inpulse.data.model.request.IdeiaIdRequest
import br.com.fiap.inpulse.data.model.response.Contribuicao
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.data.model.response.IdeiaResponse
import br.com.fiap.inpulse.data.model.response.ProgramaFuncionario
import br.com.fiap.inpulse.data.model.response.ProgramaResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val textDica: TextView = itemView.findViewById(R.id.text_dica)

        private var subscribed = false

        private lateinit var suasIdeiasAdapter: SuasIdeiasAdapter

        private val PREFS_NAME = "InPulsePrefs"
        private val KEY_USER_ID = "loggedInUserId"
        private val KEY_FUNCIONARIO_JSON = "funcionario_json"

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

            val sharedPref = itemView.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val funcionarioJson = sharedPref.getString(KEY_FUNCIONARIO_JSON, null)

            if (funcionarioJson != null) {
                val gson = Gson()
                val funcionarioData = gson.fromJson(funcionarioJson, FuncionarioResponse::class.java)

                val subscribedProgramIds = funcionarioData.programas.map { it.id }.toSet()
                this.subscribed = subscribedProgramIds.contains(programa.programa_id)
            } else {
                this.subscribed = false
            }

            if (this.subscribed) {
                btnInscrever.text = "Inscrito"
                btnInscrever.isEnabled = false
            } else {
                btnInscrever.text = "Inscrever-se"
                btnInscrever.isEnabled = true
            }

            updateEnviarIdeiaButtonState()

            val userId = sharedPref.getInt(KEY_USER_ID, -1)

            btnInscrever.setOnClickListener {

                if (subscribed) return@setOnClickListener

                lifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val newFunc = FuncionarioIdRequest(
                                funcionarios_id = userId
                            )
                            RetrofitClient.inPulseApiService.subscribePrograma(programaId = programa.programa_id, newFunc)
                                withContext(Dispatchers.Main) {
                                    subscribed = true
                                    btnInscrever.text = "Inscrito"
                                    btnInscrever.isEnabled = false
                                    updateEnviarIdeiaButtonState()

                                    val currentJson = sharedPref.getString(KEY_FUNCIONARIO_JSON, null)
                                    if (currentJson != null) {
                                        val gson = Gson()
                                        val currentFuncionarioData = gson.fromJson(currentJson, FuncionarioResponse::class.java)

                                        val novoProgramaInscrito = ProgramaFuncionario(
                                            id = programa.programa_id,
                                            nome = programa.nome_programa
                                            )
                                        val programasAtualizados = currentFuncionarioData.programas.toMutableList()
                                        programasAtualizados.add(novoProgramaInscrito)

                                        val funcionarioAtualizado = currentFuncionarioData.copy(
                                            programas = programasAtualizados
                                        )

                                        val novoJson = gson.toJson(funcionarioAtualizado)
                                        sharedPref.edit().putString(KEY_FUNCIONARIO_JSON, novoJson).apply()

                                        Toast.makeText(itemView.context, "Inscrição realizada!", Toast.LENGTH_SHORT).show()
                                        }
                                }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    itemView.context,
                                    "Erro: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                e.printStackTrace()
                                subscribed = false
                                btnInscrever.text = "Inscrever-se"
                                updateEnviarIdeiaButtonState()
                            }
                        }
                    }
                }
            }

            recyclerIdeasP.visibility = View.VISIBLE
            recyclerIdeasS.visibility = View.GONE

            suasIdeiasAdapter = SuasIdeiasAdapter(ideiasR) { ideiaClicada ->
                Toast.makeText(itemView.context, "Enviando: ${ideiaClicada.nome}", Toast.LENGTH_SHORT).show()
                lifecycleOwner.lifecycleScope.launch {
                    try {
                        val newIdeia = IdeiaIdRequest(
                            ideias_id = ideiaClicada.ideia_id
                        )
                        RetrofitClient.inPulseApiService.subscribeIdeia(programaId = programa.programa_id, newIdeia)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(itemView.context, "Ideia enviada com sucesso!", Toast.LENGTH_SHORT).show()
                            recyclerIdeasS.visibility = View.GONE
                            textDica.visibility = View.GONE
                            recyclerIdeasP.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(itemView.context, "Falha ao enviar ideia: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            recyclerIdeasS.layoutManager = GridLayoutManager(itemView.context, 2)
            recyclerIdeasS.adapter = suasIdeiasAdapter
            recyclerIdeasS.layoutManager = GridLayoutManager(itemView.context, 2)
            recyclerIdeasS.adapter = suasIdeiasAdapter

            btnEnviarIdeia.setOnClickListener {
                if (!subscribed) {
                    Toast.makeText(itemView.context, "Você precisa se inscrever no programa primeiro.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (recyclerIdeasS.visibility == View.VISIBLE) {
                    recyclerIdeasS.visibility = View.GONE
                    textDica.visibility = View.GONE
                    recyclerIdeasP.visibility = View.VISIBLE
                } else {
                    recyclerIdeasS.visibility = View.VISIBLE
                    recyclerIdeasP.visibility = View.GONE
                    textDica.visibility = View.VISIBLE
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
}