package br.com.fiap.inpulse

import android.os.Bundle
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
        configureToolbar(toolbarHub)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        loadFragment(homeFragment)

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

    private fun configureToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgWhite))
    }
}
