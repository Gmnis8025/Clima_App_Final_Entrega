package com.example.clima_app_final.clima.data

import com.example.clima_app_final.clima.domain.ClimaHoy
import com.example.clima_app_final.clima.domain.PronosticoDia

class ClimaRepositoryMock : ClimaRepository {
    override suspend fun obtenerClimaHoy(lat: Double, lon: Double): ClimaHoy {
        return ClimaHoy(
            temperaturaActual = 20.0,
            temperaturaMin = 15.0,
            temperaturaMax = 25.0,
            humedad = 60,
            descripcion = "Parcialmente nublado"
        )
    }

    override suspend fun obtenerPronostico5Dias(
        lat: Double,
        lon: Double
    ): List<PronosticoDia> {
        return listOf(
            PronosticoDia("Dom", 18.0, 27.0, "☀️"),
            PronosticoDia("Lun", 16.0, 23.0, "🌤️"),
            PronosticoDia("Mar", 14.0, 20.0, "☁️"),
            PronosticoDia("Mié", 10.0, 17.0, "🌧️"),
            PronosticoDia("Jue", 5.0, 12.0, "❄️")
        )
    }
}