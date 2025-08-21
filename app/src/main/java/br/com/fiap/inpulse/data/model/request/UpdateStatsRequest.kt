package br.com.fiap.inpulse.data.model.request

class UpdateStatsRequest(
    val pontos: Int = 0,
    val moedas: Int = 0,
    val modo_anonimo: Boolean = false,
    val selos_id: List<Int> = emptyList()
)

