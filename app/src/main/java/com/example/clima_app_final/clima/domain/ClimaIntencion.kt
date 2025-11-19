package com.example.clima_app_final.clima.domain

sealed class ClimaIntencion {
    data class CargarClima(
        val lat: Double,
        val lon: Double,
        val nombreCiudad: String
    ) : ClimaIntencion()

    object Refrescar : ClimaIntencion()

    object PrepararTextoParaCompartir : ClimaIntencion()

    object LimpiarError : ClimaIntencion()
}

