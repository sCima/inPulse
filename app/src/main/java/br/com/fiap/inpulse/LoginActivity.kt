package br.com.fiap.inpulse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_email_login)
        val etSenha = findViewById<EditText>(R.id.et_senha_login)
        val btnEntrar: AppCompatButton = findViewById(R.id.btn_login)
        val btnCadastro:  Button = findViewById(R.id.btn_cadastre)

        btnEntrar.setOnClickListener{
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            fetchFuncionarioData(email, senha) { loginSucesso ->
                if (loginSucesso) {
                    val intent = Intent(this, HubActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Email ou senha incorretos.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

    }

    fun fetchFuncionarioData(email: String, senha: String, onLoginResult: (Boolean) -> Unit) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val funcionario = RetrofitClient.inPulseApiService.getFuncionarioByEmail(email)
                    withContext(Dispatchers.Main) {
                        val isLoginValid = funcionario.senha == senha
                        onLoginResult(isLoginValid)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Falha ao buscar login: ${e.message}", Toast.LENGTH_LONG).show()
                        onLoginResult(false)
                    }
                }
            }
        }
    }

}