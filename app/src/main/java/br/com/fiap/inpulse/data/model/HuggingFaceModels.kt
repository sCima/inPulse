package br.com.fiap.inpulse.data.model

data class ClassificationRequest(val inputs: String)

typealias ClassificationResponse = List<List<LabelScore>>

data class LabelScore(
    val label: String,
    val score: Double
)