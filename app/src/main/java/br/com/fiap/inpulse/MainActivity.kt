package br.com.fiap.inpulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val PREFS_NAME = "InPulsePrefs"
    private val KEY_USER_ID = "loggedInUserId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = sharedPref.getInt(KEY_USER_ID, -1)

        if (userId != -1) {
            val intent = Intent(this, HubActivity::class.java)
            intent.putExtra(KEY_USER_ID, userId)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}