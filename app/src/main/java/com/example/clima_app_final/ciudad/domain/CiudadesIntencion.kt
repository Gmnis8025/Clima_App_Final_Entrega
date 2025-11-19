package com.example.clima_app_final.ciudad.domain

sealed class CiudadesIntencion {
    object CargarInicial : CiudadesIntencion()
    data class BuscarPorTexto(val texto: String) : CiudadesIntencion()
    data class SeleccionarCiudad(val ciudad: Ciudad) : CiudadesIntencion()
    data class BuscarPorUbicacion(val lat: Double, val lon: Double) : CiudadesIntencion()
    object LimpiarError : CiudadesIntencion()
    data class MostrarError(val mensaje: String) : CiudadesIntencion()
}
