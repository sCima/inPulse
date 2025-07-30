package br.com.fiap.inpulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.data.RetrofitClient
import br.com.fiap.inpulse.data.response.FuncionarioResponse
import br.com.fiap.inpulse.utils.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private val PREFS_NAME = "InPulsePrefs"
    private val KEY_USER_ID = "loggedInUserId"

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

            fetchFuncionarioData(email, senha) { funcionario, loginSucesso ->
                if (loginSucesso && funcionario != null) {
                    val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt(KEY_USER_ID, funcionario.funcionario_id)
                        apply()
                    }
                    val intent = Intent(this, HubActivity::class.java)
                    intent.putExtra("funcionario_data", funcionario)
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

    private fun fetchFuncionarioData(email: String, senhaDigitada: String, onLoginResult: (FuncionarioResponse?, Boolean) -> Unit) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val funcionario = RetrofitClient.inPulseApiService.getFuncionarioByEmail(email)
                    withContext(Dispatchers.Main) {
                        val isLoginValid = PasswordHasher.checkPassword(senhaDigitada, funcionario.senha)
                        onLoginResult(funcionario, isLoginValid)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Falha ao buscar login: ${e.message}", Toast.LENGTH_LONG).show()
                        onLoginResult(null, false)
                    }
                }
            }
        }
    }

}