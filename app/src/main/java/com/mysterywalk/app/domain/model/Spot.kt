package com.mysterywalk.app.domain.model

data class Spot(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val name: String?,
    val category: String?
)
