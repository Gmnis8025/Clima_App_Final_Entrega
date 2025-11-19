package com.example.clima_app_final.ciudad.data

import com.example.clima_app_final.ciudad.domain.Ciudad
import kotlin.math.abs

class CiudadesRepositoryMock : CiudadesRepository {

    private val ciudades = listOf(
        Ciudad(
            nombre = "INVENTADA",
            lat = -32.600,
            lon = -50.600,
            pais = "AR"
        ),
        Ciudad(
            nombre = "CARACAS",
            lat = -31.421,
            lon = -64.188,
            pais = "AR"
        ),
        Ciudad(
            nombre = "INVENTADACARA",
            lat = -34.9587,
            lon = -70.639,
            pais = "AR"
        ),
        Ciudad(
            nombre = "Mendoza",
            lat = -32.8895,
            lon = -68.8458,
            pais = "AR"
        ),
        Ciudad(
            nombre = "Plata",
            lat = -34.9214,
            lon = -57.9544,
            pais = "AR"
        ),
        Ciudad(
            nombre = "Mar",
            lat = -38.0055,
            lon = -57.5426,
            pais = "AR"
        ),
        Ciudad(
            nombre = "CIUDADBARO",
            lat = -40.1339,
            lon = -71.3443,
            pais = "AR"
        )
    )

    override suspend fun buscarCiudadesPorNombre(nombre: String): List<Ciudad> {
        return ciudades.filter { it.nombre.contains(nombre, ignoreCase = true) }
    }

    override suspend fun obtenerCiudadPorGeolocalizacion(lat: Double, lon: Double): Ciudad? {
        return ciudades.minByOrNull {
            abs(it.lat - lat) + abs(it.lon - lon)
        }
    }


}