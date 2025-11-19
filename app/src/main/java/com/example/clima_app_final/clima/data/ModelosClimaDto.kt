package com.example.clima_app_final.clima.data


import kotlinx.serialization.Serializable

// --- Clima actual ---

@Serializable
data class WeatherMainDto(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

@Serializable
data class WeatherDescriptionDto(
    val description: String
)

@Serializable
data class CurrentWeatherDto(
    val main: WeatherMainDto,
    val weather: List<WeatherDescriptionDto>
)

// Pronóstico 5 días (cada 3 horas)

@Serializable
data class ForecastMainDto(
    val temp_min: Double,
    val temp_max: Double
)

@Serializable
data class ForecastItemDto(
    val dt_txt: String,   // "2025-11-15 18:00:00"
    val main: ForecastMainDto
)

@Serializable
data class ForecastResponseDto(
    val list: List<ForecastItemDto>
)
