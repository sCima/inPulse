package br.com.fiap.inpulse.data.model.response

import com.google.gson.annotations.SerializedName

data class LabelScore(
    @SerializedName("label")
    val label: String,

    @SerializedName("score")
    val score: Double
)