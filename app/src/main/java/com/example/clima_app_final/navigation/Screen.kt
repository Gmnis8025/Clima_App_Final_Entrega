package com.example.clima_app_final.navigation

sealed class Screen(val route: String) {

    object Ciudades : Screen("ciudades")

    object Clima : Screen("clima/{lat}/{lon}/{nombre}") {
        fun createRoute(lat: String, lon: String, nombre: String): String {
            // Codificamos el nombre
            val nombreSafe = java.net.URLEncoder.encode(nombre, "UTF-8")
            return "clima/$lat/$lon/$nombreSafe"
        }
    }
}
