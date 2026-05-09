package com.mysterywalk.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OverpassResponseDto(
    @Json(name = "elements") val elements: List<OverpassElementDto>?
)

@JsonClass(generateAdapter = true)
data class OverpassElementDto(
    @Json(name = "id") val id: Long,
    @Json(name = "lat") val lat: Double?,
    @Json(name = "lon") val lon: Double?,
    @Json(name = "center") val center: CenterDto?, // ways や relations の場合に中心座標が入る
    @Json(name = "tags") val tags: Map<String, String>?
)

@JsonClass(generateAdapter = true)
data class CenterDto(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double
)
