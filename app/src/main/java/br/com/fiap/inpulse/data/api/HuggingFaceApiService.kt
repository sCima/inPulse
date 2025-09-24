package br.com.fiap.inpulse.data.api

import br.com.fiap.inpulse.data.model.ClassificationRequest
import br.com.fiap.inpulse.data.model.ClassificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface HuggingFaceApiService {
    @POST
    suspend fun classify( @Url url: String, @Body request: ClassificationRequest):
            Response<ClassificationResponse>
}
