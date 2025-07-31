package br.com.fiap.inpulse.viewmodel

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.RetrofitClient
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.data.request.ContribuicaoRequest
import br.com.fiap.inpulse.data.request.LikeRequest
import br.com.fiap.inpulse.data.response.Contribuicao
import br.com.fiap.inpulse.data.response.ContribuicaoResponse
import br.com.fiap.inpulse.data.response.FuncionarioResponse
import br.com.fiap.inpulse.data.response.IdeiaResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class IdeaAdapter(var ideas: MutableList<IdeiaResponse>, private val fragment: String, private val lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<IdeaAdapter.InfoViewHolder>() {

    class InfoViewHolder(
        itemView: View,
        private val fragment: String,
        private val lifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<View>(R.id.includeBar)
            .findViewById(R.id.fragment_bar_title)
        val barraTitulo: ConstraintLayout = itemView.findViewById(R.id.includeBar)
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
        var layoutCompleto: View = itemView.findViewById(R.id.layout_ideias)
        val btnEnviarCont: ImageButton = itemView.findViewById(R.id.btn_enviar_c)
        val etCont: EditText = itemView.findViewById(R.id.et_pergunta)

        private var liked = false
        private var contsVisible = false
        private var contribuicao: String = "N"
        private lateinit var contAdapter: ContAdapter

        private val PREFS_NAME = "InPulsePrefs"
        private val KEY_USER_ID = "loggedInUserId"
        private val KEY_FUNCIONARIO_JSON = "funcionario_json"

        fun bind(idea: IdeiaResponse) {
            nome.text = idea.nome
            problema.text = idea.problema
            descricao.text = idea.descricao

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            try {
                val date = inputFormat.parse(idea.data)
                if (date != null) {
                    data.text = outputFormat.format(date)
                } else {
                    data.text = "Data inválida"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                data.text = "Erro na data"
            }

            autor.text = idea.funcionario_nome
            likes.text = idea.curtidas.toString()

            if (fragment == "ProfileFragment") {
                containerInterno.visibility = View.GONE
                val params = layoutCompleto.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutCompleto.layoutParams = params

                barraTitulo.setOnClickListener {
                    if (containerInterno.visibility == View.GONE) {
                        containerInterno.visibility = View.VISIBLE
                        val params = layoutCompleto.layoutParams
                        val pixels = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            350f,  // valor em dp
                            layoutCompleto.resources.displayMetrics
                        ).toInt()
                        params.height = pixels
                        layoutCompleto.layoutParams = params
                    } else if (containerInterno.visibility == View.VISIBLE) {
                        containerInterno.visibility = View.GONE
                        val params = layoutCompleto.layoutParams
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        layoutCompleto.layoutParams = params
                    }
                }
            }

            fun sendLike(ideiaId: Int, newLikesCount: Int) {
                lifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val requestBody = LikeRequest(curtidas = newLikesCount)
                            RetrofitClient.inPulseApiService.updateIdeia(ideiaId, requestBody)
                            withContext(Dispatchers.Main) {

                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    itemView.context,
                                    "Erro: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                e.printStackTrace()
                                liked = !liked
                                likes.text = (newLikesCount - (if (liked) 1 else -1)).toString()
                                btnCurtir.setImageResource(if (liked) R.drawable.baseline_liked_24 else R.drawable.baseline_like_24)
                            }
                        }
                    }
                }
            }

            fun sendCont(ideiaId: Int, funcionarioId: Int, cont: String, nome: String) {
                lifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val requestBody = ContribuicaoRequest(ideia_id = ideiaId, funcionario_id = funcionarioId, comentario = cont)
                            val response = RetrofitClient.inPulseApiService.sendContribuicao(requestBody)
                            withContext(Dispatchers.Main) {
                                val newContribuicao = Contribuicao(
                                    coment = response.comentario,
                                    nomeAutor = response.funcionario
                                )
                                contAdapter.addItemAtTop(newContribuicao)
                                etCont.text.clear()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    itemView.context,
                                    "Erro: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            btnCurtir.setOnClickListener {
                var count = likes.text.toString().toIntOrNull() ?: 0
                liked = !liked
                count = if (liked) count + 1 else count - 1

                btnCurtir.setImageResource(
                    if (liked) R.drawable.baseline_liked_24 else R.drawable.baseline_like_24
                )
                likes.text = itemView.context.getString(R.string.number_likes, count)
                if (liked) {
                    sendLike(idea.ideia_id, 1)
                } else {
                    sendLike(idea.ideia_id, -1)
                }
                idea.curtidas += 1
            }

            containerInterno.visibility = if (contsVisible) View.GONE else View.VISIBLE
            recyclerConts.visibility = if (contsVisible) View.VISIBLE else View.GONE
            layoutContsFoot.visibility = if (contsVisible) View.VISIBLE else View.GONE
            btnVoltar.visibility = if (contsVisible) View.VISIBLE else View.GONE

            val conts = idea.contribuicoes

            contAdapter = ContAdapter(conts.toMutableList())
            recyclerConts.layoutManager = LinearLayoutManager(itemView.context)
            recyclerConts.adapter = contAdapter

            btnEnviarCont.setOnClickListener {
                val sharedPref = itemView.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val comentario = etCont.text.toString().trim()
                val funcionarioJson = sharedPref.getString(KEY_FUNCIONARIO_JSON, null) // Lê o JSON
                if (comentario.isNotEmpty()) {
                    val userId = sharedPref.getInt(KEY_USER_ID, -1)
                    val gson = Gson()
                    val funcionario = gson.fromJson(funcionarioJson, FuncionarioResponse::class.java)
                    val funcionarioNomeCompleto = "${funcionario.primeiro_nome} ${funcionario.ultimo_sobrenome}"
                    sendCont(idea.ideia_id, userId, comentario, funcionarioNomeCompleto)
                }
            }

            btnConts.setOnClickListener {
                contsVisible = !contsVisible
                if (contsVisible) {
                    containerInterno.fadeOut()
                    recyclerConts.fadeIn()
                    layoutContsFoot.fadeIn()
                    btnVoltar.fadeIn()
                } else {
                    recyclerConts.fadeOut()
                    layoutContsFoot.fadeOut()
                    btnVoltar.fadeOut()
                    containerInterno.fadeIn()
                }
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
        return InfoViewHolder(view, fragment, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(ideas[position])
    }

    override fun getItemCount(): Int = ideas.size

    fun addItemAtTop(idea: IdeiaResponse) {
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