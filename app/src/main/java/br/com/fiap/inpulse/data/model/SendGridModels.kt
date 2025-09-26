package br.com.fiap.inpulse.data.model

import com.google.gson.annotations.SerializedName

data class EmailAddress(
    @SerializedName("email") val email: String
)

data class Personalization(
    @SerializedName("to") val to: List<EmailAddress>
)

data class EmailContent(
    @SerializedName("type") val type: String = "text/plain",
    @SerializedName("value") val value: String
)

data class SendGridRequestBody(
    @SerializedName("personalizations") val personalizations: List<Personalization>,
    @SerializedName("from") val from: EmailAddress,
    @SerializedName("subject") val subject: String,
    @SerializedName("content") val content: List<EmailContent>
)