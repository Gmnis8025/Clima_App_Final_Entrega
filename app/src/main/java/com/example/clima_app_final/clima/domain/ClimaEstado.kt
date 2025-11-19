package com.example.clima_app_final.clima.domain

data class ClimaEstado(
    val cargando: Boolean = false,
    val ciudadNombre: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val climaHoy: ClimaHoy? = null,
    val pronostico: List<PronosticoDia> = emptyList(),
    val error: String? = null,
    val textoParaCompartir: String? = null
)