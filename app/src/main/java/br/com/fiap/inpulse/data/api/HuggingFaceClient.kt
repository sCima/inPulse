package br.com.fiap.inpulse.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HuggingFaceClient {

    private val hfToken = "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"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $hfToken")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummy.base.url/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: HuggingFaceApiService by lazy {
        retrofit.create(HuggingFaceApiService::class.java)
    }
}