package br.com.fiap.inpulse.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {
    private const val BASE_URL = "https://inpulse-database.azurewebsites.net/"
    private const val BASE_H_URL = "https://api-inference.huggingface.co/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val inPulseApiService: InPulseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InPulseApiService::class.java)
    }

    val huggingFaceApiService: HuggingFaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_H_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceApiService::class.java)
    }

}