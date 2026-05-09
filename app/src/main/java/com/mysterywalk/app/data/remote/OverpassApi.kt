package com.mysterywalk.app.data.remote

import com.mysterywalk.app.data.remote.dto.OverpassResponseDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OverpassApi {
    @FormUrlEncoded
    @POST("api/interpreter")
    suspend fun getSpots(
        @Field("data") query: String
    ): OverpassResponseDto

    companion object {
        const val BASE_URL = "https://overpass-api.de/"
    }
}
