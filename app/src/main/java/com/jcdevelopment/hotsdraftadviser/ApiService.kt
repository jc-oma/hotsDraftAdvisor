package com.jcdevelopment.hotsdraftadviser
//TODO CAMERA & TensorFloor
//import com.google.android.datatransport.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jcdevelopment.hotsdraftadviser.dataclasses.HotsApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiService {
    private val json = Json {
        ignoreUnknownKeys = true // Ignoriert Felder im JSON, die nicht in der data class sind
        isLenient = true          // Erlaubt etwas lockerere JSON-Formatierung
    }
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        //TODO CAMERA & TensorFloor
        /*level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {

         */
            HttpLoggingInterceptor.Level.BODY
        //}
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private const val BASE_URL = "https://jc-oma.github.io/"

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val hotsApi = retrofit.create(HotsApiService::class.java)
}