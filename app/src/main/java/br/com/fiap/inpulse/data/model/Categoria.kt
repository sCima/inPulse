package br.com.fiap.inpulse.data.model

import androidx.annotation.DrawableRes

data class Categoria(
    val id: Int,
    val nome: String,
    @DrawableRes val icone: Int
)