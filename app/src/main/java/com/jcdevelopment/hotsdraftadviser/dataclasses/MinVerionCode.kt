package com.jcdevelopment.hotsdraftadviser.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

@Serializable
data class MinVerionCode(
    @SerialName("minVersionCode")
    val minVersionCode: Int? = null
)

// Das ist deine API-Definition
interface HotsApiService {
    @GET("setup.json")
    suspend fun getMinVersionCode(): MinVerionCode
}