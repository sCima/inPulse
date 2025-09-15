package br.com.fiap.inpulse.features.hub

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.features.chatbot.ChatbotActivity
import br.com.fiap.inpulse.features.login.LoginActivity
import br.com.fiap.inpulse.features.newidea.NovaIdeiaActivity
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.features.hub.home.HomeFragment
import br.com.fiap.inpulse.features.hub.home.NavigationListener
import br.com.fiap.inpulse.features.hub.profile.ProfileFragment
import br.com.fiap.inpulse.features.hub.ranking.RankingFragment
import br.com.fiap.inpulse.features.hub.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson

interface ToolbarController {
    fun setToolbarForProfile(data: FuncionarioResponse)
    fun resetToolbarToDefault()
}

class HubActivity : AppCompatActivity(), NavigationListener, ToolbarController {

    override fun navigateToProfile(funcionarioId: Int) {
        // Cria uma NOVA instância do ProfileFragment para não interferir com o perfil do usuário logado
        val visitorProfileFragment = ProfileFragment()

        // Cria um Bundle para passar o ID do funcionário a ser visitado
        val bundle = Bundle()
        bundle.putInt("profile_user_id", funcionarioId)
        visitorProfileFragment.arguments = bundle

        // Carrega o fragment com os dados do visitante
        loadFragment(visitorProfileFragment)
    }
    private lateinit var toolbarHub: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()
    private val rankingFragment = RankingFragment()
    private val settingsFragment = SettingsFragment()

    private var funcionarioData: FuncionarioResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)

        val funcionarioFromIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("funcionario_data", FuncionarioResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("funcionario_data") as? FuncionarioResponse
        }

        funcionarioData = funcionarioFromIntent ?: run {
            val json = getSharedPreferences("InPulsePrefs", Context.MODE_PRIVATE)
                .getString("funcionario_json", null)
            json?.let { Gson().fromJson(it, FuncionarioResponse::class.java) }
        }

        funcionarioData?.let {
            val bundle = Bundle()
            bundle.putParcelable("funcionario_profile_data", it)
            profileFragment.arguments = bundle
            homeFragment.arguments = bundle
            settingsFragment.arguments = bundle
        } ?: run {
            Toast.makeText(this, "Erro ao carregar dados do usuário. Faça login novamente.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        toolbarHub = findViewById(R.id.toolbar_hub)
        configureToolbar(toolbarHub)

        val btnChatbot: ImageButton = toolbarHub.findViewById(R.id.toolbar_button)
        btnChatbot.setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }


        val btnPerfil = toolbarHub.findViewById<ShapeableImageView>(R.id.toolbar_image)
        btnPerfil.setOnClickListener {
            loadFragment(profileFragment)
        }

        loadFragment(homeFragment)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.profile -> {
                    loadFragment(profileFragment)
                    true
                }
                R.id.idea -> {
                    val intent = Intent(this, NovaIdeiaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.ranking -> {
                    loadFragment(rankingFragment)
                    true
                }
                R.id.settings -> {
                    loadFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_bottom,
                0
            )
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun configureToolbar(
        toolbar: Toolbar,
        isPerfil: Boolean = false,
        dataOverride: FuncionarioResponse? = null // Novo parâmetro para dados do perfil
    ) {
        setSupportActionBar(toolbar)
        val toolbarButton: ImageButton = findViewById(R.id.toolbar_button)
        val toolbarTitle: TextView? = toolbar.findViewById(R.id.toolbar_text)
        var tierColor: Int = R.color.bronze

        // ▼▼▼ LÓGICA DE DECISÃO DE DADOS ▼▼▼
        // Se a chamada forneceu dados (dataOverride), use-os.
        // Caso contrário, use o 'funcionarioData' padrão (usuário logado).
        val dataToUse = dataOverride ?: this.funcionarioData
        // ▲▲▲ FIM DA LÓGICA DE DECISÃO ▲▲▲

        dataToUse?.let { funcionario ->
            val primeiroNome = funcionario.primeiro_nome.trim()
            val ultimoSobrenome = funcionario.ultimo_sobrenome.trim()

            val nomeFormatado = if (primeiroNome.isNotEmpty() && ultimoSobrenome.isNotEmpty()) {
                val primeiraLetraSobrenome = ultimoSobrenome.first().uppercaseChar()
                "$primeiroNome $primeiraLetraSobrenome."
            } else if (primeiroNome.isNotEmpty()) {
                primeiroNome
            } else {
                "Funcionario"
            }
            toolbarTitle?.text = nomeFormatado

            tierColor = when (funcionario.tier) {
                "Prata" -> R.color.silver
                "Ouro" -> R.color.gold
                else -> R.color.bronze
            }
        } ?: run {
            toolbarTitle?.text = "InPulse"
            tierColor = R.color.bronze
        }

        toolbarButton.setBackgroundColor(getColor(R.color.bgWhite))
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgWhite))

        if (isPerfil) {
            supportActionBar?.setBackgroundDrawable(getDrawable(tierColor))
            toolbarButton.setBackgroundColor(getColor(tierColor))
        }
    }

    // 3. IMPLEMENTE OS MÉTODOS DA INTERFACE
    override fun setToolbarForProfile(data: FuncionarioResponse) {
        // Chama a função refatorada com isPerfil=true e os dados do perfil específico
        configureToolbar(toolbarHub, true, data)
    }

    override fun resetToolbarToDefault() {
        // Chama a função refatorada no modo padrão (usará o 'funcionarioData' do usuário logado)
        configureToolbar(toolbarHub, false, null)
    }
}
