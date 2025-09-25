package br.com.fiap.inpulse.features.chatbot

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import br.com.fiap.inpulse.R
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import br.com.fiap.inpulse.data.model.ChatMessage
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.cx.v3.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatbotActivity : AppCompatActivity() {

    private val projectId = "gen-lang-client-0121528891"
    private val locationId = "us-central1"
    private val agentId = "e78750b7-665e-47c4-b036-84a004fcd416"
    private val languageCode = "pt-br"
    private var googleCredentials: GoogleCredentials? = null

    private lateinit var recyclerChat: RecyclerView
    private lateinit var etPergunta: EditText
    private lateinit var btnEnviar: ImageButton
    private lateinit var btnIdeias: TextView
    private lateinit var btnApp: TextView
    private lateinit var btnTags: TextView

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val sessionId = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        initViews()
        setupRecyclerView()
        setupClickListeners()

        lifecycleScope.launch(Dispatchers.IO) {
            setupDialogflow()
        }
    }

    private fun initViews() {
        recyclerChat = findViewById(R.id.recycler_chat)
        etPergunta = findViewById(R.id.et_pergunta)
        btnEnviar = findViewById(R.id.btn_enviar)
        btnIdeias = findViewById(R.id.btn_ideias)
        btnApp = findViewById(R.id.btn_app)
        btnTags = findViewById(R.id.btn_tags)

        val btnVoltar: ImageButton = findViewById(R.id.btn_voltar_chatbot)
        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList, lifecycleScope)
        recyclerChat.adapter = chatAdapter

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerChat.layoutManager = layoutManager
    }

    private fun setupClickListeners() {
        btnEnviar.setOnClickListener {
            val userMessage = etPergunta.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                addMessageToChat(userMessage, true)
                sendMessageToBot(userMessage)
                etPergunta.text.clear()
            }
        }

        btnIdeias.setOnClickListener {
            val question = "Me fale sobre o que são ideias de inovação."
            addMessageToChat(question, true)
            sendMessageToBot(question)
        }

        btnApp.setOnClickListener {
            val question = "Me fale de maneira geral como funciona o aplicativo."
            addMessageToChat(question, true)
            sendMessageToBot(question)
        }

        btnTags.setOnClickListener {
            val question = "Me fale o que são as tags (categorias de ideia)."
            addMessageToChat(question, true)
            sendMessageToBot(question)
        }
    }

    private fun addMessageToChat(text: String, isSentByUser: Boolean) {
        runOnUiThread {
            messageList.add(ChatMessage(text, isSentByUser))
            val novaPosicao = messageList.size - 1
            chatAdapter.notifyItemInserted(novaPosicao)
            recyclerChat.scrollToPosition(novaPosicao)
        }
    }

    private fun setupDialogflow() {
        try {
            val credentialsStream = resources.openRawResource(R.raw.dialogflow_token)
            googleCredentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

            val creds = googleCredentials

            creds?.refreshIfExpired()

            val sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .setEndpoint("$locationId-dialogflow.googleapis.com:443")
                .build()

            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, locationId, agentId, sessionId)

            Log.d("AUTH_DEBUG", "Dialogflow configurado com sucesso.")

        } catch (e: Exception) {
            e.printStackTrace()
            addMessageToChat("Erro ao conectar com o bot. Tente novamente mais tarde.", false)
        }
    }

    private fun sendMessageToBot(message: String) {
        if (sessionsClient == null) return

        val textInput = TextInput.newBuilder().setText(message).build()
        val queryInput = QueryInput.newBuilder().setText(textInput).setLanguageCode(languageCode).build()

        val request = DetectIntentRequest.newBuilder()
            .setSession(sessionName.toString())
            .setQueryInput(queryInput)
            .build()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val creds = googleCredentials
                creds?.refreshIfExpired()

                val response = sessionsClient?.detectIntent(request)
                val botResponses = response?.queryResult?.responseMessagesList

                withContext(Dispatchers.Main) {
                    if (botResponses.isNullOrEmpty()) {
                        addMessageToChat("Não entendi o que você disse. Pode tentar de outra forma?", false)
                    } else {
                        botResponses.forEach { responseMessage ->
                            if (responseMessage.hasText()) {
                                val botText = responseMessage.text.textList.joinToString("\n")
                                addMessageToChat(botText, false)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addMessageToChat("Desculpe, não consegui processar sua mensagem.", false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionsClient?.close()
    }
}