package br.com.fiap.inpulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val PREFS_NAME = "InPulsePrefs"
    private val KEY_USER_ID = "loggedInUserId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = sharedPref.getInt(KEY_USER_ID, -1)

        if (userId != -1) {
            lifecycleScope.launch {
                try {
                    val funcionario = withContext(Dispatchers.IO) {
                        RetrofitClient.inPulseApiService.getFuncionarioById(userId)
                    }
                    val intent = Intent(this@MainActivity, HubActivity::class.java)
                    intent.putExtra("funcionario_data", funcionario)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}