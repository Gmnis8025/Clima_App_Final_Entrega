package com.example.clima_app_final.clima.domain


data class ClimaHoy(
    val temperaturaActual: Double,
    val temperaturaMin: Double,
    val temperaturaMax: Double,
    val humedad: Int,
    val descripcion: String
)

/**
 * Pronóstico de un día icono lo uso para mostrar ☀️, 🌧️, ⛅, ❄️,
 */
data class PronosticoDia(
    val dia: String,
    val temperaturaMin: Double,
    val temperaturaMax: Double,
    val iconoEmoji: String
)
