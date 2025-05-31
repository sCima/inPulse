package br.com.fiap.inpulse.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Programa (

    val nome: String,
    val dataFim: String = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date()),
    val dataInicio: String = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date()),
    val desc: String
)