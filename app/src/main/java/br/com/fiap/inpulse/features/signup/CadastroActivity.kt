package br.com.fiap.inpulse.features.signup

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.request.FuncionarioRequest
import br.com.fiap.inpulse.features.login.LoginActivity
import br.com.fiap.inpulse.utils.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroActivity : AppCompatActivity() {

    private lateinit var etPrimeiroNome: EditText
    private lateinit var etUltimoSobrenome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmeSenha: EditText
    private lateinit var switchModoAnonimo: Switch
    private lateinit var btnCadastrar: AppCompatButton
    private lateinit var btnVoltar: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        etPrimeiroNome = findViewById(R.id.et_primeiro_nome_cadastro)
        etUltimoSobrenome = findViewById(R.id.et_ultimo_sobrenome_cadastro)
        etEmail = findViewById(R.id.et_email_cadastro)
        etSenha = findViewById(R.id.et_senha_cadastro)
        etConfirmeSenha = findViewById(R.id.et_confirme_senha_cadastro)
        switchModoAnonimo = findViewById(R.id.switch_modo_anonimo_cadastro)
        btnCadastrar = findViewById(R.id.btn_cadastrar)
        btnVoltar = findViewById(R.id.btn_voltar_cadastro)

        btnVoltar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnCadastrar.setOnClickListener {
            val primeiroNome = etPrimeiroNome.text.toString().trim()
            val ultimoSobrenome = etUltimoSobrenome.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()
            val confirmeSenha = etConfirmeSenha.text.toString().trim()
            val modoAnonimo = switchModoAnonimo.isChecked

            if (primeiroNome.isBlank() || ultimoSobrenome.isBlank() || email.isBlank() || senha.isBlank() || confirmeSenha.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmeSenha) {
                etConfirmeSenha.error = "As senhas não coincidem."
                Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "E-mail inválido."
                Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val senhaCript = PasswordHasher.hashPassword(senha)

            val funcionarioRequest = FuncionarioRequest(
                primeiro_nome = primeiroNome,
                ultimo_sobrenome = ultimoSobrenome,
                email = email,
                senha = senhaCript,
                modo_anonimo = modoAnonimo
            )

            cadastrarFuncionarioApi(funcionarioRequest)
        }
    }

    private fun cadastrarFuncionarioApi(request: FuncionarioRequest) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val novoFuncionario = RetrofitClient.inPulseApiService.cadastrarFuncionario(request)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CadastroActivity, "Bem-vindo(a), ${novoFuncionario.primeiro_nome}!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@CadastroActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CadastroActivity, "Falha no cadastro: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}