package com.example.clima_app_final.ciudad.data

import com.example.clima_app_final.ciudad.domain.Ciudad

interface CiudadesRepository {
    suspend fun buscarCiudadesPorNombre(nombre: String): List<Ciudad>
    suspend fun obtenerCiudadPorGeolocalizacion(lat: Double, lon: Double): Ciudad?
}