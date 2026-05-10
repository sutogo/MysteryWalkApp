package com.mysterywalk.app.data.remote

import com.mysterywalk.app.data.remote.dto.WikimediaResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WikimediaApi {
    @GET("w/api.php")
    suspend fun searchImageByLocation(
        @Query("action") action: String = "query",
        @Query("generator") generator: String = "geosearch",
        @Query("ggscoord") coords: String, // e.g., "lat|lon"
        @Query("ggsradius") radius: Int = 500, // 500 meters
        @Query("ggslimit") limit: Int = 1,
        @Query("prop") prop: String = "imageinfo",
        @Query("iiprop") iiprop: String = "url",
        @Query("format") format: String = "json"
    ): WikimediaResponseDto
}
