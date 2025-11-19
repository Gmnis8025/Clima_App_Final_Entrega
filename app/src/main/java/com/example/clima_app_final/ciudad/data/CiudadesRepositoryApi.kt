package com.example.clima_app_final.ciudad.data

import com.example.clima_app_final.ciudad.domain.Ciudad
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class CiudadesRepositoryApi : CiudadesRepository {

    private val cliente = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private val apiKey = "b8cecad9bfd5c4416eacdbf082d8f523"


    override suspend fun buscarCiudadesPorNombre(nombre: String): List<Ciudad> {

        if (nombre.isBlank()) return emptyList()

        val respuesta = cliente.get("https://api.openweathermap.org/geo/1.0/direct") {
            parameter("q", nombre)
            parameter("limit", 5)
            parameter("appid", apiKey)
        }

        val lista: List<CiudadDto> = respuesta.body()

        return lista.map {
            Ciudad(
                nombre = it.name,
                pais = it.country,
                lat = it.lat,
                lon = it.lon
            )
        }
    }


    override suspend fun obtenerCiudadPorGeolocalizacion(
        lat: Double,
        lon: Double
    ): Ciudad? {
        val respuesta = cliente.get("https://api.openweathermap.org/geo/1.0/reverse") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("limit", 1)
            parameter("appid", apiKey)
        }

        val lista: List<CiudadDto> = respuesta.body()

        return lista.firstOrNull()?.let {
            Ciudad(
                nombre = it.name,
                pais = it.country,
                lat = it.lat,
                lon = it.lon
            )
        }
    }
}

@Serializable
data class CiudadDto(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)
