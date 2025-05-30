package br.com.fiap.inpulse

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

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