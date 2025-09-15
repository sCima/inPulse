package br.com.fiap.inpulse.data.model.request

import com.google.gson.annotations.SerializedName

data class HuggingFaceRequest(
    @SerializedName("inputs")
    val inputs: String
)