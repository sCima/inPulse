package br.com.fiap.inpulse.features.chatbot

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.features.hub.HubActivity

class ChatbotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        val btnVoltar: ImageButton = findViewById(R.id.btn_voltar_chatbot)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, HubActivity::class.java)
            startActivity(intent)
        }
    }
}