package br.com.fiap.inpulse.data.api

import br.com.fiap.inpulse.data.model.request.HuggingFaceRequest
import br.com.fiap.inpulse.data.model.response.LabelScore
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Header

interface HuggingFaceApiService {
    companion object {
        const val MODELS_ID = "lucaslimb/eurofarma"
        const val MODELO_ID = "lucaslimb/eurofarmaO"
        const val MODELC_ID = "lucaslimb/eurofarmaC"
        const val MODELU_ID = "lucaslimb/eurofarmaU"
    }

    @POST("models/{modelId}")
    suspend fun getSetor(
        @Path("modelId", encoded = true) modelId: String = MODELS_ID,
        @Body request: HuggingFaceRequest,
        @Header("Authorization") token: String
    ): Response<List<List<LabelScore>>>

    @POST("models/{modelId}")
    suspend fun getObjetivo(
        @Path("modelId", encoded = true) modelId: String = MODELO_ID,
        @Body request: HuggingFaceRequest,
        @Header("Authorization") token: String
    ): Response<List<List<LabelScore>>>

    @POST("models/{modelId}")
    suspend fun getComplexidade(
        @Path("modelId", encoded = true) modelId: String = MODELC_ID,
        @Body request: HuggingFaceRequest,
        @Header("Authorization") token: String
    ): Response<List<List<LabelScore>>>

    @POST("models/{modelId}")
    suspend fun getUrgencia(
        @Path("modelId", encoded = true) modelId: String = MODELU_ID,
        @Body request: HuggingFaceRequest,
        @Header("Authorization") token: String
    ): Response<List<List<LabelScore>>>
}