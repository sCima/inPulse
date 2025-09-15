package br.com.fiap.inpulse.features.hub.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.request.SenhaRequest
import br.com.fiap.inpulse.data.model.request.UpdateStatsRequest
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.features.hub.ToolbarController
import br.com.fiap.inpulse.features.login.LoginActivity
import br.com.fiap.inpulse.utils.PasswordHasher
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private var funcionarioData: FuncionarioResponse? = null
    private lateinit var passwordContainer: View

    private var toolbarListener: ToolbarController? = null // Use a interface da Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // ... (seu código de onAttach existente)
        if (context is ToolbarController) {
            toolbarListener = context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onResume() {
        super.onResume()
        toolbarListener?.resetToolbarToDefault()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAnonimo = view.findViewById<MaterialButton>(R.id.settins_anonimo)
        val btnTema = view.findViewById<MaterialButton>(R.id.settings_theme)
        val toggleGroupAnonimo = view.findViewById<MaterialButtonToggleGroup>(R.id.toggle_group_anonimo)
        val toggleGroupTema = view.findViewById<MaterialButtonToggleGroup>(R.id.toggle_group_theme)
        val btnPassword = view.findViewById<MaterialButton>(R.id.settings_password)
        passwordContainer = view.findViewById(R.id.password_change_container)
        val editTextPassword = view.findViewById<EditText>(R.id.edit_text_new_password)
        val editTextOldPassword = view.findViewById<EditText>(R.id.edit_text_old_password)
        val btnSavePassword = view.findViewById<Button>(R.id.button_save_password)
        val tvAnonimo = view.findViewById<TextView>(R.id.text_desc_anonimo)
        val btnLogoff = view.findViewById<MaterialButton>(R.id.settings_logoff)
        val btnFotoPerfil = view.findViewById<MaterialButton>(R.id.settings_photo)

        arguments?.let { bundle ->
            funcionarioData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("funcionario_profile_data", FuncionarioResponse::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable("funcionario_profile_data")
            }
        }

        funcionarioData?.let { funcionario ->
            if (funcionario.modo_anonimo) {
                toggleGroupAnonimo.check(R.id.button_anonimo)
            } else {
                toggleGroupAnonimo.check(R.id.button_publico)
            }
        }

        btnAnonimo.setOnClickListener {
            toggleGroupAnonimo.isVisible = !toggleGroupAnonimo.isVisible
            if(toggleGroupAnonimo.isVisible) tvAnonimo.visibility = View.VISIBLE else tvAnonimo.visibility = View.GONE
        }

        btnTema.setOnClickListener {
            toggleGroupTema.isVisible = !toggleGroupTema.isVisible
        }

        btnFotoPerfil.setOnClickListener {

        }

        toggleGroupAnonimo.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val novoModoAnonimo = when (checkedId) {
                    R.id.button_anonimo -> true
                    R.id.button_publico -> false
                    else -> return@addOnButtonCheckedListener
                }
                if (funcionarioData?.modo_anonimo != novoModoAnonimo) {
                    updateAnonymousMode(novoModoAnonimo)
                }
            }
        }

        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> toggleGroupTema.check(R.id.button_light)
            AppCompatDelegate.MODE_NIGHT_YES -> toggleGroupTema.check(R.id.button_dark)
            else -> toggleGroupTema.check(R.id.button_light)
        }

        toggleGroupTema.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val themeMode = when (checkedId) {
                    R.id.button_light -> AppCompatDelegate.MODE_NIGHT_NO
                    R.id.button_dark -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> return@addOnButtonCheckedListener
                }

                AppCompatDelegate.setDefaultNightMode(themeMode)

                saveThemePreference(themeMode)
            }
        }

        btnPassword.setOnClickListener {
            passwordContainer.isVisible = !passwordContainer.isVisible
        }

        btnSavePassword.setOnClickListener {
            val newPassword = editTextPassword.text.toString()
            val oldPassword = editTextOldPassword.text.toString()

            if (newPassword.isNotBlank()) {
                val oldMatches = funcionarioData?.senha?.let { senha ->
                    PasswordHasher.checkPassword(oldPassword,
                        senha
                    )
                }
                if(oldMatches == true){
                    if(oldPassword == newPassword){
                        Toast.makeText(context, "A nova senha deve ser diferente da atual.", Toast.LENGTH_SHORT).show()
                    } else {
                        updateSenha(newPassword)
                    }
                } else (
                    Toast.makeText(context, "A senha atual está incorreta.", Toast.LENGTH_SHORT).show()
                )
            } else {
                Toast.makeText(context, "Por favor, digite uma nova senha.", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogoff.setOnClickListener {
            performLogoff()
        }
    }

    private fun updateAnonymousMode(isAnonymous: Boolean) {
        val funcionarioId = funcionarioData?.funcionario_id ?: return

        lifecycleScope.launch {
            try {
                val requestBody = UpdateStatsRequest(modo_anonimo = isAnonymous)
                val updatedFuncionarioResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.inPulseApiService.updateFuncionarioStats(funcionarioId, requestBody)
                }
                withContext(Dispatchers.Main) {
                    funcionarioData?.modo_anonimo = updatedFuncionarioResponse.modo_anonimo
                    Toast.makeText(context, "Preferência de perfil atualizada!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Falha ao atualizar. Tente novamente.", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    funcionarioData?.let {
                        val toggleGroupAnonimo = view?.findViewById<MaterialButtonToggleGroup>(R.id.toggle_group_anonimo)
                        if (it.modo_anonimo) {
                            toggleGroupAnonimo?.check(R.id.button_anonimo)
                        } else {
                            toggleGroupAnonimo?.check(R.id.button_publico)
                        }
                    }
                }
            }
        }
    }

    private fun updateSenha(novaSenha: String) {
        val funcionarioId = funcionarioData?.funcionario_id ?: return
        lifecycleScope.launch {
            try {
                val senhaCript = PasswordHasher.hashPassword(novaSenha)
                val requestBody = SenhaRequest(senhaCript)
                val updatedSenhaRequest = withContext(Dispatchers.IO) {
                    RetrofitClient.inPulseApiService.updateSenha(funcionarioId, requestBody)
                }
                withContext(Dispatchers.Main) {
                    funcionarioData?.senha = updatedSenhaRequest.senha
                    passwordContainer.isVisible = !passwordContainer.isVisible
                    Toast.makeText(context, "Senha alterada!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Falha ao alterar. Tente novamente.", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun performLogoff() {
        val sharedPreferences = requireContext().getSharedPreferences("InPulsePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun saveThemePreference(themeMode: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("theme_mode", themeMode).apply()
    }
}