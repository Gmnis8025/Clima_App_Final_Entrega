package com.example.clima_app_final.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clima_app_final.ciudad.domain.Ciudad
import com.example.clima_app_final.ciudad.presentation.CiudadesPage
import com.example.clima_app_final.clima.presentation.ClimaPage
import com.example.clima_app_final.storage.CiudadDataStore
import kotlinx.coroutines.launch

@Composable
fun AppRouter(
    ciudadGuardada: Ciudad?
) {

    val navController = rememberNavController()
    val dataStore = CiudadDataStore(navController.context)

    val scope = rememberCoroutineScope()

    val startDestination = if (ciudadGuardada != null) {
        Screen.Clima.createRoute(
            lat = ciudadGuardada.lat.toString(),
            lon = ciudadGuardada.lon.toString(),
            nombre = ciudadGuardada.nombre
        )
    } else {
        Screen.Ciudades.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Ciudades.route) {
            CiudadesPage(
                onSeleccionarCiudad = { ciudad ->

                    //  guarda la ciudad
                    scope.launch {
                        dataStore.guardarCiudad(
                            nombre = ciudad.nombre,
                            pais = ciudad.pais,
                            lat = ciudad.lat,
                            lon = ciudad.lon
                        )
                    }

                    navController.navigate(
                        Screen.Clima.createRoute(
                            lat = ciudad.lat.toString(),
                            lon = ciudad.lon.toString(),
                            nombre = ciudad.nombre
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.Clima.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType }
            )
        ) { backStack ->

            val lat = backStack.arguments!!.getString("lat")!!.toDouble()
            val lon = backStack.arguments!!.getString("lon")!!.toDouble()

            val nombreCodificado = backStack.arguments!!.getString("nombre") ?: ""

            val nombre = try {
                java.net.URLDecoder.decode(nombreCodificado, "UTF-8")
            } catch (e: Exception) {
                nombreCodificado
            }

            ClimaPage(
                lat = lat,
                lon = lon,
                nombreCiudad = nombre,
                onCambiarCiudad = { navController.navigate(Screen.Ciudades.route) }
            )
        }

    }
}
