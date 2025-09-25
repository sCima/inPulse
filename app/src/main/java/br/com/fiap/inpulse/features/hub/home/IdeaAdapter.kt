package br.com.fiap.inpulse.features.hub.home

import android.content.Context
import coil.load
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.data.model.request.ContribuicaoRequest
import br.com.fiap.inpulse.data.model.request.LikeRequest
import br.com.fiap.inpulse.data.model.response.Contribuicao
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.data.model.response.IdeiaResponse
import br.com.fiap.inpulse.utils.CategoriaMapper
import br.com.fiap.inpulse.utils.fadeIn
import br.com.fiap.inpulse.utils.fadeOut
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

interface OnProfileClickListener {
    fun onProfileClick(funcionarioId: Int)
}

class IdeaAdapter(var ideas: MutableList<IdeiaResponse>,
                  private val fragment: String,
                  private val lifecycleOwner: LifecycleOwner,
                  private val profileClickListener: OnProfileClickListener?) :
    RecyclerView.Adapter<IdeaAdapter.InfoViewHolder>() {

    class InfoViewHolder(
        itemView: View,
        private val fragment: String,
        private val lifecycleOwner: LifecycleOwner,
        private val profileClickListener: OnProfileClickListener?
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
        val imgSetor: ImageView = itemView.findViewById(R.id.imgSetor)
        val imgTipo: ImageView = itemView.findViewById(R.id.imgTipo)
        val imgComplex: ImageView = itemView.findViewById(R.id.imgComplex)
        val imgUrgencia: ImageView = itemView.findViewById(R.id.imgUrgencia)


        val imagemIdeia: ImageView = itemView.findViewById(R.id.imagemIdeia)

        private var contsVisible = false
        private lateinit var contAdapter: ContAdapter

        private val KEY_LIKED_IDEAS = "liked_ideas_set"
        private val PREFS_NAME = "InPulsePrefs"
        private val KEY_USER_ID = "loggedInUserId"
        private val KEY_FUNCIONARIO_JSON = "funcionario_json"

        fun bind(idea: IdeiaResponse) {
            nome.text = idea.nome
            problema.text = idea.problema
            descricao.text = idea.descricao
            val apiKeys = idea.categoriasIcone

            val predictionMap = mutableMapOf<String, String>()

            for (key in apiKeys) {
                when {
                    CategoriaMapper.mapaSetor.containsKey(key) -> predictionMap["setor"] = key

                    CategoriaMapper.mapaObjetivo.containsKey(key) -> predictionMap["objetivo"] = key

                    CategoriaMapper.mapaComplexidade.containsKey(key) -> predictionMap["complexidade"] = key

                    CategoriaMapper.mapaUrgencia.containsKey(key) -> predictionMap["urgencia"] = key
                }
            }

            val categoriasMapeadas = CategoriaMapper.mapearPredicoes(predictionMap)

            categoriasMapeadas["setor"]?.let { categoria ->
                imgSetor.setImageResource(categoria.icone)
                TooltipCompat.setTooltipText(imgSetor, categoria.nome)
            }

            categoriasMapeadas["objetivo"]?.let { categoria ->
                imgTipo.setImageResource(categoria.icone)
                TooltipCompat.setTooltipText(imgTipo, categoria.nome)
            }

            categoriasMapeadas["complexidade"]?.let { categoria ->
                imgComplex.setImageResource(categoria.icone)
                TooltipCompat.setTooltipText(imgComplex, categoria.nome)
            }

            categoriasMapeadas["urgencia"]?.let { categoria ->
                imgUrgencia.setImageResource(categoria.icone)
                TooltipCompat.setTooltipText(imgUrgencia, categoria.nome)
            }

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

            autor.text = idea.funcionario_nome.primeiro_nome

            autor.setOnClickListener {
                profileClickListener?.onProfileClick(idea.funcionario_nome.id)
            }

            likes.text = idea.curtidas.toString()

            if (!idea.imagem.isNullOrEmpty()) {
                imagemIdeia.visibility = View.VISIBLE
                imagemIdeia.load(idea.imagem) {
                    crossfade(true)
                    placeholder(R.drawable.loading_placeholder)
                    error(R.drawable.error_placeholder)
                }
            } else {
                imagemIdeia.visibility = View.GONE
            }

            if (fragment == "ProfileFragment") {
                containerInterno.visibility = View.GONE
                val params = layoutCompleto.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutCompleto.layoutParams = params

                barraTitulo.setBackgroundResource(R.drawable.shape_fragment_body_rounded_p)

                barraTitulo.setOnClickListener {
                    if (containerInterno.visibility == View.GONE) {
                        barraTitulo.setBackgroundResource(R.drawable.shape_fragment_bar)

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
                        barraTitulo.setBackgroundResource(R.drawable.shape_fragment_body_rounded_p)
                        val params = layoutCompleto.layoutParams
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        layoutCompleto.layoutParams = params
                    }
                }
            }

            val prefs = itemView.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val likedIdeasSet = prefs.getStringSet(KEY_LIKED_IDEAS, emptySet()) ?: emptySet()
            val isLiked = likedIdeasSet.contains(idea.ideia_id.toString())

            btnCurtir.setImageResource(
                if (isLiked) R.drawable.baseline_liked_24 else R.drawable.baseline_like_24
            )

            fun sendLike(ideiaId: Int, newLikesCount: Int) {
                lifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val requestBody = LikeRequest(curtidas = newLikesCount)
                            RetrofitClient.inPulseApiService.updateIdeia(ideiaId, requestBody)
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(itemView.context, "Erro de rede: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }

            btnCurtir.setOnClickListener {
                val sharedPrefs = itemView.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                val likedIdeas = sharedPrefs.getStringSet(KEY_LIKED_IDEAS, emptySet())?.toMutableSet() ?: mutableSetOf()
                val ideaIdString = idea.ideia_id.toString()

                var currentLikesCount = likes.text.toString().toIntOrNull() ?: 0

                if (likedIdeas.contains(ideaIdString)) {
                    likedIdeas.remove(ideaIdString)
                    btnCurtir.setImageResource(R.drawable.baseline_like_24)
                    currentLikesCount--
                    sendLike(idea.ideia_id, -1)

                } else {
                    likedIdeas.add(ideaIdString)
                    btnCurtir.setImageResource(R.drawable.baseline_liked_24)
                    currentLikesCount++
                    sendLike(idea.ideia_id, 1)

                }

                likes.text = currentLikesCount.toString()

                editor.putStringSet(KEY_LIKED_IDEAS, likedIdeas)
                editor.apply()
            }

            fun sendCont(ideiaId: Int, funcionarioId: Int, cont: String, nome: String) {
                btnEnviarCont.isEnabled = false;
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
                                btnEnviarCont.isEnabled = true;
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
                            btnEnviarCont.isEnabled = true;
                        }
                    }
                }
            }

            containerInterno.visibility = if (contsVisible) View.GONE else View.VISIBLE
            recyclerConts.visibility = if (contsVisible) View.VISIBLE else View.GONE
            layoutContsFoot.visibility = if (contsVisible) View.VISIBLE else View.GONE
            btnVoltar.visibility = if (contsVisible) View.VISIBLE else View.GONE

            val conts = idea.contribuicoes
            val latestFiveConts = conts.takeLast(5).reversed()
            contAdapter = ContAdapter(latestFiveConts.toMutableList())
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
        return InfoViewHolder(view, fragment, lifecycleOwner, profileClickListener)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(ideas[position])
    }

    override fun getItemCount(): Int = ideas.size
}