package br.com.fiap.inpulse.features.newidea

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.request.IdeiaRequest
import br.com.fiap.inpulse.features.hub.HubActivity
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentD
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentI
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentP
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentR
import br.com.fiap.inpulse.features.newidea.fragments.IdeaInfoProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NovaIdeiaActivity : AppCompatActivity() {

    private val ideaFragmentP = IdeaFragmentP()
    private val ideaFragmentD = IdeaFragmentD()
    private val ideaFragmentI = IdeaFragmentI()
    private val ideaFragmentR = IdeaFragmentR()
    private var etapaAtual = 0
    private val infosIdea = Bundle()
    private lateinit var btnContinuar: AppCompatButton

    private val fragments = listOf(
        ideaFragmentP,
        ideaFragmentD,
        ideaFragmentI,
        ideaFragmentR
    )

    private val PREFS_NAME = "InPulsePrefs"
    private val KEY_USER_ID = "loggedInUserId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_ideia)

        btnContinuar = findViewById(R.id.btn_continuar)

        carregarFragmentoAtual()

        val toolbarIdea: Toolbar = findViewById(R.id.toolbar_idea)
        configureToolbar(toolbarIdea)

        btnContinuar.setOnClickListener {
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.containerIdea)

            if (fragmentAtual is IdeaInfoProvider) {
                val novasInfos = fragmentAtual.enviarDados()
                infosIdea.putAll(novasInfos)
            }

            if (etapaAtual < fragments.size - 1) {
                etapaAtual++
                val proximoFragmento = fragments[etapaAtual]

                if (proximoFragmento is IdeaFragmentR) {
                    proximoFragmento.arguments = infosIdea
                }
                carregarFragmentoAtual()
            } else {
                val nomeIdeia = infosIdea.getString("etTitulo") ?: ""
                val problemaIdeia = infosIdea.getString("resumoProblema") ?: ""
                val descricaoIdeia = infosIdea.getString("resumoSolucao") ?: ""
                val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val funcionarioId = sharedPref.getInt(KEY_USER_ID, -1)
                val categorias = listOf(1, 2, 3, 4)

                val ideiaRequest: IdeiaRequest = IdeiaRequest(
                    nome = nomeIdeia,
                    problema = problemaIdeia,
                    descricao = descricaoIdeia,
                    imagem = "null",
                    funcionario_id = funcionarioId,
                    categorias_id = categorias
                )

                enviarIdeia(ideiaRequest)
            }
        }

        val btnVoltar: ImageButton = findViewById(R.id.btn_voltar_ideia)
        btnVoltar.setOnClickListener {
            if (etapaAtual > 0) {
                etapaAtual--
                carregarFragmentoAtual()
            } else {
                val intent = Intent(this, HubActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun configureToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgBottomNav))
    }

    private fun carregarFragmentoAtual(): Fragment {
        val fragment = fragments[etapaAtual]
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerIdea, fragment)
            .commit()

        if (fragment is IdeaFragmentR) {
            btnContinuar.text = "Enviar"
        } else {
            btnContinuar.text = "Continuar"
        }

        return fragment
    }

    private fun enviarIdeia(ideiaRequest: IdeiaRequest) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.inPulseApiService.sendIdeia(ideiaRequest)

                    withContext(Dispatchers.Main) {
                        if (response != null) {
                            Toast.makeText(
                                this@NovaIdeiaActivity,
                                "Sucesso",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@NovaIdeiaActivity,
                                "Erro ao enviar ideia",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@NovaIdeiaActivity,
                            "Falha na conexão: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}