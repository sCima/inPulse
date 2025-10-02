package br.com.fiap.inpulse.data.api.azure

import br.com.fiap.inpulse.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AzureRetrofitClient {

    private const val BASE_URL = "https://aipulse.eastus.inference.ml.azure.com/"

    private val API_KEY = BuildConfig.AZURE_KEY

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