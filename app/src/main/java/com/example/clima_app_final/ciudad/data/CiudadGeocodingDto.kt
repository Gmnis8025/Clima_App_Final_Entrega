package com.example.clima_app_final.ciudad.data

import kotlinx.serialization.Serializable

@Serializable
data class CiudadGeocodingDto(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String = "",
    val state: String? = ""
)
