package com.example.clima_app_final.clima.data

import com.example.clima_app_final.clima.domain.ClimaHoy
import com.example.clima_app_final.clima.domain.PronosticoDia

interface ClimaRepository {
    suspend fun obtenerClimaHoy(lat: Double, lon: Double): ClimaHoy
    suspend fun obtenerPronostico5Dias(lat: Double, lon: Double): List<PronosticoDia>
}
