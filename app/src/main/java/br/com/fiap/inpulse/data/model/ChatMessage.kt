package br.com.fiap.inpulse.data.model

class ChatMessage (
    val text: String,
    val isSentByUser: Boolean,
    var hasBeenAnimated: Boolean = false
)