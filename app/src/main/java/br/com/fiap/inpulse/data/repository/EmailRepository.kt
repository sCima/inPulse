package br.com.fiap.inpulse.data.repository

import br.com.fiap.inpulse.data.api.sendgrid.SendGridRetrofitClient
import br.com.fiap.inpulse.data.model.EmailAddress
import br.com.fiap.inpulse.data.model.EmailContent
import br.com.fiap.inpulse.data.model.Personalization
import br.com.fiap.inpulse.data.model.SendGridRequestBody

class EmailRepository {
    private val apiService = SendGridRetrofitClient.instance

    suspend fun sendEmail(
        recipientEmail: String,
        subject: String,
        body: String
    ) {
        val fromEmail = EmailAddress("rm98632@fiap.com.br")
        val toEmail = EmailAddress(recipientEmail)
        val personalization = Personalization(listOf(toEmail))
        val content = EmailContent(value = body)

        val requestBody = SendGridRequestBody(
            personalizations = listOf(personalization),
            from = fromEmail,
            subject = subject,
            content = listOf(content)
        )

        val apiKey = "Bearer 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"

        try {
            apiService.sendEmail(apiKey, requestBody)
        } catch (e: Exception) {
            println("Erro na chamada de API: ${e.message}")
        }
    }
}