package br.com.fiap.inpulse.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AzureRetrofitClient {

    private const val BASE_URL = "https://aipulse.eastus.inference.ml.azure.com/"

    private val API_KEY = "G2d4MNp53VsW1R0TG7Z2EqGvfkrBoJWSXJ98e8GFXVC5p2pmj6N8JQQJ99BIAAAAAAAAAAAAINFRAZML3bnc"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(newRequest)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: AzureMLService by lazy {
        retrofit.create(AzureMLService::class.java)
    }
}