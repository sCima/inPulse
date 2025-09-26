package br.com.fiap.inpulse.data.api.azure

import br.com.fiap.inpulse.data.model.ClassificationRequest
import br.com.fiap.inpulse.data.model.LabelScore
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AzureMLService {

    @POST("score")
    suspend fun classifyWithHeader(
        @Header("azureml-model-deployment") deploymentName: String,
        @Body request: ClassificationRequest
    ): Response<List<LabelScore>>
}
