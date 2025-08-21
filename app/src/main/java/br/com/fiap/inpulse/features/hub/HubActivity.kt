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
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.features.chatbot.ChatbotActivity
import br.com.fiap.inpulse.features.login.LoginActivity
import br.com.fiap.inpulse.features.newidea.NovaIdeiaActivity
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.features.hub.home.HomeFragment
import br.com.fiap.inpulse.features.hub.profile.ProfileFragment
import br.com.fiap.inpulse.features.hub.ranking.RankingFragment
import br.com.fiap.inpulse.features.hub.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson

class HubActivity : AppCompatActivity() {

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

        val toolbarHub: Toolbar = findViewById(R.id.toolbar_hub)
        configureToolbar(toolbarHub)

        val btnChatbot: ImageButton = toolbarHub.findViewById(R.id.toolbar_button)
        btnChatbot.setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }


        val btnPerfil = toolbarHub.findViewById<ShapeableImageView>(R.id.toolbar_image)
        btnPerfil.setOnClickListener {
            loadFragment(profileFragment)
            configureToolbar(toolbarHub, true)
        }

        loadFragment(homeFragment)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    configureToolbar(toolbarHub)
                    true
                }
                R.id.profile -> {
                    loadFragment(profileFragment)
                    configureToolbar(toolbarHub, true)
                    true
                }
                R.id.idea -> {
                    val intent = Intent(this, NovaIdeiaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.ranking -> {
                    loadFragment(rankingFragment)
                    configureToolbar(toolbarHub)
                    true
                }
                R.id.settings -> {
                    loadFragment(settingsFragment)
                    configureToolbar(toolbarHub)
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

    private fun configureToolbar(toolbar: Toolbar, isPerfil: Boolean = false) {
        setSupportActionBar(toolbar)
        val toolbarButton: ImageButton = findViewById(R.id.toolbar_button)
        val toolbarTitle: TextView? = toolbar.findViewById(R.id.toolbar_text)
        funcionarioData?.let { funcionario ->
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
        } ?: run {
            toolbarTitle?.text = "InPulse"
        }
        toolbarButton.setBackgroundColor(getColor(R.color.bgWhite))
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgWhite))

        if(isPerfil){
            val tierColorResId = when (funcionarioData?.tier) {
                "Bronze" -> R.color.bronze
                "Prata" -> R.color.silver
                "Ouro" -> R.color.gold
                else -> R.color.bronze
            }
            supportActionBar?.setBackgroundDrawable(getDrawable(tierColorResId))
            toolbarButton.setBackgroundColor(getColor(tierColorResId))
        }
    }
}
