package br.com.fiap.inpulse.features.newidea

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.HuggingFaceApiService
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.ClassificationRequest
import br.com.fiap.inpulse.data.model.request.IdeiaRequest
import br.com.fiap.inpulse.features.hub.HubActivity
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentDescricao
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentImg
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentMissoes
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentProblema
import br.com.fiap.inpulse.features.newidea.fragments.IdeaFragmentResumo
import br.com.fiap.inpulse.features.newidea.fragments.IdeaInfoProvider
import br.com.fiap.inpulse.features.newidea.fragments.OnCategoriasMapeadasListener
import br.com.fiap.inpulse.utils.AzureConstants
import br.com.fiap.inpulse.utils.CategoriaMapper
import br.com.fiap.inpulse.utils.ImageSelectionListener
import br.com.fiap.inpulse.utils.uploadImageToAzure
import com.azure.storage.blob.BlobClientBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class NovaIdeiaActivity : AppCompatActivity(), ImageSelectionListener,
    OnCategoriasMapeadasListener {

    private val ideaFragmentProblema = IdeaFragmentProblema()
    private val ideaFragmentDescricao = IdeaFragmentDescricao()
    private val ideaFragmentImg = IdeaFragmentImg()
    private val ideaFragmentResumo = IdeaFragmentResumo()
    private val ideaFragmentMissoes = IdeaFragmentMissoes()
    private var etapaAtual = 0
    private val infosIdea = Bundle()
    private lateinit var btnContinuar: AppCompatButton
    private var imageUrlFromAzure: String? = null //
    private var idsDasCategorias: List<Int>? = null

    override fun onCategoriasProntas(ids: List<Int>) {
        this.idsDasCategorias = ids
        Log.d("NovaIdeiaActivity", "IDs das categorias da IA recebidos: $ids")
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let { uri ->
                    val fragment = supportFragmentManager.findFragmentById(R.id.containerIdea) as? IdeaFragmentImg
                    fragment?.updateImagePreviewFromUri(uri)

                    lifecycleScope.launch {
                        val nomeDoArquivo = "ideia-${System.currentTimeMillis()}.jpg"
                        val urlFinal = uploadImageToAzure(this@NovaIdeiaActivity, uri, nomeDoArquivo)

                        if (urlFinal != null) {
                            imageUrlFromAzure = urlFinal
                            Log.d("AzureUpload", "Upload com sucesso! URL: $urlFinal")
                            Toast.makeText(this@NovaIdeiaActivity, "Imagem enviada!", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("AzureUpload", "Falha no upload da imagem.")
                            Toast.makeText(this@NovaIdeiaActivity, "Erro ao enviar imagem.", Toast.LENGTH_LONG).show()
                            fragment?.updateImagePreviewFromUri(null)
                        }
                    }
                }
            }
        }

    private val fragments = listOf(
        ideaFragmentMissoes,
        ideaFragmentProblema,
        ideaFragmentDescricao,
        ideaFragmentImg,
        ideaFragmentResumo
    )

    private val PREFS_NAME = "InPulsePrefs"
    private val KEY_USER_ID = "loggedInUserId"
    private val KEY_FUNCIONARIO_JSON = "funcionario_json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_ideia)

        btnContinuar = findViewById(R.id.btn_continuar)

        carregarFragmentoAtual()

        val toolbarIdea: Toolbar = findViewById(R.id.toolbar_idea)
        configureToolbar(toolbarIdea)

        btnContinuar.setOnClickListener {
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.containerIdea)

            if (fragmentAtual is IdeaInfoProvider) {
                val novasInfos = fragmentAtual.enviarDados()
                infosIdea.putAll(novasInfos)
            }

            if (etapaAtual < fragments.size - 1) {
                etapaAtual++
                carregarFragmentoAtual()
            } else {
                val nomeIdeia = infosIdea.getString("etTitulo") ?: ""
                val problemaIdeia = infosIdea.getString("resumoProblema") ?: ""
                val descricaoIdeia = infosIdea.getString("resumoSolucao") ?: ""
                val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val funcionarioId = sharedPref.getInt(KEY_USER_ID, -1)
                val categorias = if(idsDasCategorias != null) idsDasCategorias else listOf(104, 107, 110, 112)

                val ideiaRequest = IdeiaRequest(
                    nome = nomeIdeia,
                    problema = problemaIdeia,
                    descricao = descricaoIdeia,
                    imagem = imageUrlFromAzure,
                    funcionario_id = funcionarioId,
                    categorias_id = categorias!!
                )

                enviarIdeia(ideiaRequest)
            }
        }

        val btnVoltar: ImageButton = findViewById(R.id.btn_voltar_ideia)
        btnVoltar.setOnClickListener {
            if (etapaAtual > 0) {
                etapaAtual--
                carregarFragmentoAtual()
            } else {
                val intent = Intent(this, HubActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun configureToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.bgBottomNav))
    }

    private fun carregarFragmentoAtual() {
        val fragment = fragments[etapaAtual]

        if (fragment is IdeaFragmentImg) {
            fragment.setImageSelectionListener(this)
        }

        if (fragment is IdeaFragmentResumo) {
            fragment.arguments = infosIdea
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerIdea, fragment as Fragment)
            .commit()

        if (fragment is IdeaFragmentResumo) {
            btnContinuar.text = "Enviar"
        } else {
            btnContinuar.text = "Continuar"
        }
    }

    override fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    override fun onImageSelected(base64Image: String?) {
        if(base64Image == null){
            this.imageUrlFromAzure = null
            (supportFragmentManager.findFragmentById(R.id.containerIdea) as? IdeaFragmentImg)?.updateImagePreviewFromUri(null)
        }
    }

    private suspend fun uploadImageToAzure(context: Context, imageUri: Uri, blobName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val blobEndpoint = "https://${AzureConstants.STORAGE_ACCOUNT_NAME}.blob.core.windows.net"
                val blobUrlWithSas = "$blobEndpoint/${AzureConstants.CONTAINER_NAME}/$blobName${AzureConstants.SAS_TOKEN}"

                val blobClient = BlobClientBuilder().endpoint(blobUrlWithSas).buildClient()

                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    blobClient.upload(inputStream, inputStream.available().toLong(), true)
                    return@withContext "$blobEndpoint/${AzureConstants.CONTAINER_NAME}/$blobName"
                }

                return@withContext null

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    private fun enviarIdeia(ideiaRequest: IdeiaRequest) {

        lifecycleScope.launch {
            try {
                val responseIdeia = withContext(Dispatchers.IO) {
                    RetrofitClient.inPulseApiService.sendIdeia(ideiaRequest)
                }

                if (responseIdeia == null) {
                    throw Exception("Falha ao criar a ideia. A resposta da API foi nula.")
                }

                Log.d("UserDataSync", "Ideia enviada. Buscando dados atualizados do usuário...")
                val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val funcionarioId = sharedPref.getInt(KEY_USER_ID, -1)

                if (funcionarioId == -1) {
                    Log.w("UserDataSync", "ID do usuário não encontrado. Não foi possível atualizar os dados locais.")
                }

                val funcionarioAtualizado = withContext(Dispatchers.IO) {
                    RetrofitClient.inPulseApiService.getFuncionarioById(funcionarioId)
                }

                val gson = Gson()
                val novoJson = gson.toJson(funcionarioAtualizado)
                sharedPref.edit().putString(KEY_FUNCIONARIO_JSON, novoJson).apply()
                Log.d("UserDataSync", "JSON do funcionário atualizado com sucesso no SharedPreferences.")

                Toast.makeText(this@NovaIdeiaActivity, "Ideia enviada com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@NovaIdeiaActivity, HubActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@NovaIdeiaActivity,
                    "Falha na operação: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }


}

