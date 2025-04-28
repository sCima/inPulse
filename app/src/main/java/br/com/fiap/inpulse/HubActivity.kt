package br.com.fiap.inpulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
        configureToolbar(toolbarHub)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, homeFragment)
            .commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, homeFragment)
                        .commit()
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, profileFragment)
                        .commit()
                    true
                }
                R.id.ranking -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, rankingFragment)
                        .commit()
                    true
                }
                R.id.settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, settingsFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }

    }

    private fun configureToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgBlue))
    }
}
