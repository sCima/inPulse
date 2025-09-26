package br.com.fiap.inpulse.data.api.sendgrid

import br.com.fiap.inpulse.data.model.SendGridRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SendGridApiService {
    @POST("v3/mail/send")
    suspend fun sendEmail(
        @Header("Authorization") apiKey: String,
        @Body requestBody: SendGridRequestBody
    ): Response<Unit>
}