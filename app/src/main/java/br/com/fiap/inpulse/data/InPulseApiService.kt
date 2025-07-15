package br.com.fiap.inpulse.data

import br.com.fiap.inpulse.model.response.IdeiaResponse
import retrofit2.http.GET

interface InPulseApiService {

    @GET("ideias")
    suspend fun loadIdeias(): List<IdeiaResponse>

}
