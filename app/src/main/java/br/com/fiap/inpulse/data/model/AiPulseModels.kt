package br.com.fiap.inpulse.data.model

data class ClassificationRequest(val inputs: String)

data class LabelScore(
    val label: String,
    val score: Double
)