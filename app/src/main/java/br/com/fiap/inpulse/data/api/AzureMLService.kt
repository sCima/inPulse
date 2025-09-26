package br.com.fiap.inpulse.data.api

import br.com.fiap.inpulse.data.model.ClassificationRequest
import br.com.fiap.inpulse.data.model.LabelScore
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AzureMLService {

        @POST("deployments/{deployment_name}/score")
        suspend fun classify(
            @Path("deployment_name") deploymentName: String,
            @Body request: ClassificationRequest
        ): Response<List<LabelScore>>

        @POST("score")
        suspend fun classifyDefault(
            @Body request: ClassificationRequest
        ): Response<List<LabelScore>>
}
