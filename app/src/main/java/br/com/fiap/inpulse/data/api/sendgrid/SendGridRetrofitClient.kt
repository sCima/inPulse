package br.com.fiap.inpulse.data.api.sendgrid

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SendGridRetrofitClient {
    private const val BASE_URL = "https://api.sendgrid.com/"

    val instance: SendGridApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(SendGridApiService::class.java)
    }
}