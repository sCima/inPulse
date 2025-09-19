package br.com.fiap.inpulse.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FuncionarioResponse(
    val funcionario_id: Int,
    val primeiro_nome: String,
    val ultimo_sobrenome: String,
    val email: String,
    var senha: String,
    val pontos: Int,
    val moedas: Int,
    val tier: String,
    var modo_anonimo: Boolean,
    val imagem_funcionario: String?,
    val ideias: List<IdeiaFuncionario>,
    val programas: List<ProgramaFuncionario>,
    val selos: List<Selo>,
    val itens: List<Item>,
    val logs: List<String>,
    var valorOrdenacao: String? = null
) : Parcelable

@Parcelize
data class IdeiaFuncionario(
    val ideia_id: Int,
    val nome: String,
    val problema: String,
    val descricao: String,
    val imagem: String?,
    val data: String,
    val curtidas: Int,
    val programas_nome: List<ProgramaFuncionario>,
    val categoriasIcone: List<String>
) : Parcelable

@Parcelize
data class Selo(
    val nome: String,
    val descricao: String
) : Parcelable

@Parcelize
data class ProgramaFuncionario(
    val id: Int,
    val nome: String
) : Parcelable

@Parcelize
data class Item(
    val id: Int,
    val nome: String
) : Parcelable