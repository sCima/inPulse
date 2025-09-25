package br.com.fiap.inpulse.features.chatbot

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.model.ChatMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ChatAdapter(private val messages: MutableList<ChatMessage>,
                  private val lifecycleScope: LifecycleCoroutineScope
) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.message_text)
        var typingJob: Job? = null
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByUser) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SENT) {
            val view = layoutInflater.inflate(R.layout.item_message_sent, parent, false)
            MessageViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.item_message_received, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        holder.typingJob?.cancel()

        val textoFormatado = formatarTextoComNegrito(message.text)

        if (message.isSentByUser) {
            holder.messageText.text = textoFormatado
        } else {
            if (message.hasBeenAnimated) {
                holder.messageText.text = textoFormatado
            } else {
                holder.messageText.text = ""
                holder.typingJob = lifecycleScope.launch {
                    val textoOriginal = message.text
                    val textoParaAnimar = textoOriginal.replace("**", "")

                    textoParaAnimar.forEach { char ->
                        holder.messageText.append(char.toString())
                        delay(15)
                    }

                    holder.messageText.text = formatarTextoComNegrito(textoOriginal)
                    message.hasBeenAnimated = true
                }
            }
        }
    }

    override fun onViewRecycled(holder: MessageViewHolder) {
        super.onViewRecycled(holder)
        holder.typingJob?.cancel()
    }

    override fun getItemCount() = messages.size
}

fun formatarTextoComNegrito(texto: String): SpannableStringBuilder {
    val spannable = SpannableStringBuilder(texto)

    val pattern = Pattern.compile("\\*\\*(.*?)\\*\\*")
    val matcher = pattern.matcher(texto)

    val matches = mutableListOf<Pair<Int, Int>>()
    while (matcher.find()) {
        matches.add(Pair(matcher.start(1), matcher.end(1)))
    }

    for (match in matches) {
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            match.first,
            match.second,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    var i = spannable.length - 1
    while (i >= 0) {
        if (spannable[i] == '*' && i > 0 && spannable[i - 1] == '*') {
            spannable.delete(i - 1, i + 1)
            i--
        }
        i--
    }

    return spannable
}