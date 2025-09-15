package br.com.fiap.inpulse.data.repository

import android.util.Log
import br.com.fiap.inpulse.data.api.HuggingFaceApiService
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.request.HuggingFaceRequest
import br.com.fiap.inpulse.data.model.response.LabelScore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Response

class PredictionRepository {

    private val apiService: HuggingFaceApiService = RetrofitClient.huggingFaceApiService

    // GUARDE SEU TOKEN AQUI
    // O ideal seria guardar em um local mais seguro, mas para testes,
    // uma constante privada é suficiente.
    private val HF_TOKEN = "Bearer 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" // <-- MUDE AQUI

    private val TAG = "PredictionRepository"

    suspend fun getPredictions(description: String): Map<String, String> {
        val request = HuggingFaceRequest(inputs = description)

        Log.d(TAG, "Iniciando 4 chamadas concorrentes para a API com autenticação...")

        return coroutineScope {
            // Passe o token para cada chamada
            val setorDeferred = async { apiService.getSetor(request = request, token = HF_TOKEN) }
            val objetivoDeferred = async { apiService.getObjetivo(request = request, token = HF_TOKEN) }
            val complexidadeDeferred = async { apiService.getComplexidade(request = request, token = HF_TOKEN) }
            val urgenciaDeferred = async { apiService.getUrgencia(request = request, token = HF_TOKEN) }

            // ... O resto da função permanece igual
            val setorResponse = setorDeferred.await()
            val objetivoResponse = objetivoDeferred.await()
            val complexidadeResponse = complexidadeDeferred.await()
            val urgenciaResponse = urgenciaDeferred.await()

            Log.d(TAG, "Todas as chamadas da API foram concluídas. Processando resultados...")

            mapOf(
                "setor" to extractBestLabel("setor", setorResponse),
                "objetivo" to extractBestLabel("objetivo", objetivoResponse),
                "complexidade" to extractBestLabel("complexidade", complexidadeResponse),
                "urgencia" to extractBestLabel("urgencia", urgenciaResponse)
            )
        }
    }

    // A função extractBestLabel não precisa de alterações
    private fun extractBestLabel(categoryName: String, response: Response<List<List<LabelScore>>>): String {
        // ... código sem alterações
        if (response.isSuccessful) {
            val scores = response.body()?.firstOrNull()
            Log.i(TAG, "[$categoryName] Sucesso! Código: ${response.code()}, Resposta: $scores")

            if (!scores.isNullOrEmpty()) {
                return scores.maxByOrNull { it.score }?.label ?: "Indefinido"
            } else {
                Log.w(TAG, "[$categoryName] Resposta bem-sucedida, mas o corpo está vazio.")
                return "Resposta Vazia"
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Corpo do erro vazio"
            Log.e(TAG, "[$categoryName] Erro na API! Código: ${response.code()}, Mensagem: ${response.message()}, Corpo do Erro: $errorBody")
            return "Erro API (${response.code()})"
        }
    }
}