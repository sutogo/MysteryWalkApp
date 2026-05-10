package com.mysterywalk.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WikimediaResponseDto(
    @Json(name = "query") val query: WikimediaQueryDto?
)

@JsonClass(generateAdapter = true)
data class WikimediaQueryDto(
    @Json(name = "pages") val pages: Map<String, WikimediaPageDto>?
)

@JsonClass(generateAdapter = true)
data class WikimediaPageDto(
    @Json(name = "imageinfo") val imageinfo: List<WikimediaImageInfoDto>?
)

@JsonClass(generateAdapter = true)
data class WikimediaImageInfoDto(
    @Json(name = "url") val url: String
)
