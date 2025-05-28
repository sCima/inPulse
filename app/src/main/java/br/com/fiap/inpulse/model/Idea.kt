package br.com.fiap.inpulse.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Idea (
    val nome: String,
    val problema: String,
    val descricao: String,
    val data: String = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date()),
    val autor: String,
    val likes: Int
) {
    constructor(nome: String, problema: String) : this(
        nome = nome,
        problema = problema,
        descricao = "",
        autor = "",
        data = "",
        likes = 0
    )
}

