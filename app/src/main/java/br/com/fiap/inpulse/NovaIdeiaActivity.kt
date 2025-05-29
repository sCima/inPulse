package br.com.fiap.inpulse

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import br.com.fiap.inpulse.fragments.idea.IdeaFragmentD
import br.com.fiap.inpulse.fragments.idea.IdeaFragmentI
import br.com.fiap.inpulse.fragments.idea.IdeaFragmentP
import br.com.fiap.inpulse.fragments.idea.IdeaFragmentR
import br.com.fiap.inpulse.fragments.idea.IdeaInfoProvider

class NovaIdeiaActivity : AppCompatActivity() {

    private val ideaFragmentP = IdeaFragmentP()
    private val ideaFragmentD = IdeaFragmentD()
    private val ideaFragmentI = IdeaFragmentI()
    private val ideaFragmentR = IdeaFragmentR()
    private var etapaAtual = 0
    private val infosIdea = Bundle()

    private val fragments = listOf(
        ideaFragmentP,
        ideaFragmentD,
        ideaFragmentI,
        ideaFragmentR
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_ideia)

        carregarFragmentoAtual()

        val toolbarIdea: Toolbar = findViewById(R.id.toolbar_idea)
        configureToolbar(toolbarIdea)

        val btnContinuar: AppCompatButton = findViewById(R.id.btn_continuar)
        btnContinuar.setOnClickListener{
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
            }
        }

        val btnVoltar: ImageButton = findViewById(R.id.btn_voltar)
        btnVoltar.setOnClickListener {
            if (etapaAtual > 0) {
                etapaAtual--
                carregarFragmentoAtual()
            } else {
                val intent = Intent(this, HubActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun configureToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgBottomNav))
    }

    private fun carregarFragmentoAtual() {
        val fragment = fragments[etapaAtual]
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerIdea, fragment)
            .commit()
    }
}