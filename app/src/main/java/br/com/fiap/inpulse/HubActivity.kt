package br.com.fiap.inpulse

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.fragments.HomeFragment
import br.com.fiap.inpulse.fragments.ProfileFragment
import br.com.fiap.inpulse.fragments.RankingFragment
import br.com.fiap.inpulse.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HubActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()
    private val rankingFragment = RankingFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)

        val toolbarHub: Toolbar = findViewById(R.id.toolbar_hub)
        configureToolbar(toolbarHub, false)

        val btnChatbot: ImageButton = toolbarHub.findViewById(R.id.toolbar_button)
        btnChatbot.setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }

        loadFragment(homeFragment)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    configureToolbar(toolbarHub, false)
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
                    configureToolbar(toolbarHub, false)
                    true
                }
                R.id.settings -> {
                    loadFragment(settingsFragment)
                    configureToolbar(toolbarHub, false)
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

    private fun configureToolbar(toolbar: Toolbar, isPerfil: Boolean) {
        setSupportActionBar(toolbar)
        val toolbarButton: ImageButton = findViewById(R.id.toolbar_button)
        toolbarButton.setBackgroundColor(getColor(R.color.bgWhite))
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgWhite))
        if(isPerfil){
            supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bronze))
            toolbarButton.setBackgroundColor(getColor(R.color.bronze))
        }
    }
}
