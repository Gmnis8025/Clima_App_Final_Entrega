package com.example.clima_app_final.ciudad.domain

data class CiudadesEstado(
    val cargando: Boolean = false,
    val error: String? = null,
    val busqueda: String = "",
    val ciudades: List<Ciudad> = emptyList()
)
