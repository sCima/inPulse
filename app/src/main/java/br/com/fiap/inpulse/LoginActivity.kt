package br.com.fiap.inpulse

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_email_login)
        val email = etEmail.text.toString().trim()
        val etSenha = findViewById<EditText>(R.id.et_senha_login)
        val senha = etSenha.text.toString().trim()
        val btnEntrar: AppCompatButton = findViewById(R.id.btn_login)

        btnEntrar.setOnClickListener{
//            if(email == "adm@email.com"){
//                if(senha == "123"){
//                    val intent = Intent(this, HubActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    etSenha.error = "Senha incorreta"
//                    return@setOnClickListener
//                }
//            } else {
//                etEmail.error = "Email incorreto"
//                return@setOnClickListener
//            }
        }

    }
}