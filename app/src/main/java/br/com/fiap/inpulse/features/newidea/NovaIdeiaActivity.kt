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
import br.com.fiap.inpulse.utils.CategoriaMapper
import br.com.fiap.inpulse.utils.ImageSelectionListener
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
    private var imageBase64: String? = null
    private var idsDasCategorias: List<Int>? = null

    override fun onCategoriasProntas(ids: List<Int>) {
        this.idsDasCategorias = ids
        Log.d("NovaIdeiaActivity", "IDs das categorias da IA recebidos: $ids")
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                        lifecycleScope.launch(Dispatchers.IO) {
                            val compressedBitmap = resizeAndCompressBitmap(bitmap, 300, 40)
                            val base64 = bitmapToBase64(compressedBitmap)
                            withContext(Dispatchers.Main) {
                                imageBase64 = base64
                                (supportFragmentManager.findFragmentById(R.id.containerIdea) as? IdeaFragmentImg)?.updateImagePreview(imageBase64)
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro ao carregar imagem: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
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

    private val categoriaNomeParaIdMap = mapOf(
        // Setor
        "atef" to 102, "pel" to 103, "geral" to 104, "ped" to 105,
        // Objetivo
        "oa" to 106, "inv" to 107, "sustentabilidade" to 108,
        // Complexidade
        "baixa" to 109, "media" to 110, "alta" to 111,
        // Urgência
        "programada" to 112, "prioritaria" to 113, "urgente" to 114
    )

    private val PREFS_NAME = "InPulsePrefs"
    private val KEY_USER_ID = "loggedInUserId"

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
                val proximoFragmento = fragments[etapaAtual]

                if (proximoFragmento is IdeaFragmentResumo) {
                    infosIdea.putString("imagemBase64", imageBase64)
                    proximoFragmento.arguments = infosIdea
                }
                carregarFragmentoAtual()
            } else {
                if (idsDasCategorias == null) {
                    Toast.makeText(this, "Aguarde a classificação da IA...", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val nomeIdeia = infosIdea.getString("etTitulo") ?: ""
                val problemaIdeia = infosIdea.getString("resumoProblema") ?: ""
                val descricaoIdeia = infosIdea.getString("resumoSolucao") ?: ""
                val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val funcionarioId = sharedPref.getInt(KEY_USER_ID, -1)
                val categorias = idsDasCategorias!!

                val ideiaRequest = IdeiaRequest(
                    nome = nomeIdeia,
                    problema = problemaIdeia,
                    descricao = descricaoIdeia,
                    imagem = imageBase64,
                    funcionario_id = funcionarioId,
                    categorias_id = categorias
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
        this.imageBase64 = base64Image
        (supportFragmentManager.findFragmentById(R.id.containerIdea) as? IdeaFragmentImg)?.updateImagePreview(base64Image)
    }

    private fun resizeAndCompressBitmap(bitmap: Bitmap, maxWidth: Int, quality: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val ratio: Float = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxWidth
            newHeight = (newWidth / ratio).toInt()
        } else {
            newHeight = maxWidth
            newWidth = (newHeight * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    private fun enviarIdeia(ideiaRequest: IdeiaRequest) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.inPulseApiService.sendIdeia(ideiaRequest)

                    withContext(Dispatchers.Main) {
                        if (response != null) {
                            val intent = Intent(this@NovaIdeiaActivity, HubActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@NovaIdeiaActivity,
                                "Erro ao enviar ideia",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@NovaIdeiaActivity,
                            "Falha na conexão: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }


}

