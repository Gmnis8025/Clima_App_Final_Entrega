package com.example.clima_app_final.clima.data

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate
import com.example.clima_app_final.clima.domain.PronosticoDia
import com.example.clima_app_final.clima.domain.ClimaHoy


class ClimaRepositoryApi : ClimaRepository {

    private val cliente = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val apiKey = "b8cecad9bfd5c4416eacdbf082d8f523"

    override suspend fun obtenerClimaHoy(lat: Double, lon: Double): ClimaHoy {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("appid", apiKey)
            parameter("units", "metric")
            parameter("lang", "es")
        }

        if (respuesta.status == HttpStatusCode.Companion.OK) {
            val dto: CurrentWeatherDto = respuesta.body()
            val main = dto.main
            val desc = dto.weather.firstOrNull()?.description ?: "Sin descripción"

            return ClimaHoy(
                temperaturaActual = main.temp,
                temperaturaMin = main.temp_min,
                temperaturaMax = main.temp_max,
                humedad = main.humidity,
                descripcion = desc.replaceFirstChar { it.uppercase() }
            )
        } else {
            throw Exception("Error al obtener clima actual (${respuesta.status.value})")
        }
    }

    override suspend fun obtenerPronostico5Dias(
        lat: Double,
        lon: Double
    ): List<PronosticoDia> {

        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/forecast") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("appid", apiKey)
            parameter("units", "metric")
            parameter("lang", "es")
        }

        if (respuesta.status != HttpStatusCode.Companion.OK) {
            throw Exception("Error al obtener pronóstico (${respuesta.status.value})")
        }

        val dto: ForecastResponseDto = respuesta.body()


        val mapaPorDia = mutableMapOf<LocalDate, MutableList<ForecastItemDto>>()

        dto.list.forEach { item ->
            val fecha = LocalDate.parse(item.dt_txt.substring(0, 10)) // "2025-11-15"
            val lista = mapaPorDia.getOrPut(fecha) { mutableListOf() }
            lista.add(item)
        }

        val diasOrdenados = mapaPorDia.keys.sorted().take(5)

        return diasOrdenados.map { fecha ->
            val itemsDia = mapaPorDia[fecha] ?: emptyList()
            val min = itemsDia.minOfOrNull { it.main.temp_min } ?: 0.0
            val max = itemsDia.maxOfOrNull { it.main.temp_max } ?: 0.0

            val diaCorto = when (fecha.dayOfWeek) {
                DayOfWeek.MONDAY -> "Lun"
                DayOfWeek.TUESDAY -> "Mar"
                DayOfWeek.WEDNESDAY -> "Mié"
                DayOfWeek.THURSDAY -> "Jue"
                DayOfWeek.FRIDAY -> "Vie"
                DayOfWeek.SATURDAY -> "Sáb"
                DayOfWeek.SUNDAY -> "Dom"
            }

            val emoji = emojiParaTemperaturas(min, max)

            PronosticoDia(
                dia = diaCorto,
                temperaturaMin = min,
                temperaturaMax = max,
                iconoEmoji = emoji
            )
        }
    }

    /**
     *  ELIGE un emoji en base a la temperatura máxima.
     */
    private fun emojiParaTemperaturas(min: Double, max: Double): String {
        return when {
            max >= 30 -> "☀️"
            max >= 24 -> "🌤️"
            max >= 18 -> "☁️"
            max >= 10 -> "🌧️"
            else -> "❄️"
        }
    }
}
