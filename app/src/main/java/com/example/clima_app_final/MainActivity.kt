package com.example.clima_app_final
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.clima_app_final.navigation.AppRouter
import com.example.clima_app_final.storage.CiudadDataStore
import com.example.clima_app_final.ciudad.domain.Ciudad
import com.example.clima_app_final.ui.theme.Clima_App_FinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dataStore = CiudadDataStore(this)

        setContent {

            val ciudadGuardadaDS by dataStore.ciudadGuardada.collectAsState(initial = null)

            val ciudadGuardada: Ciudad? = ciudadGuardadaDS?.let {
                Ciudad(
                    nombre = it.nombre,
                    pais = it.pais,
                    lat = it.lat,
                    lon = it.lon
                )
            }

            Clima_App_FinalTheme {
                AppRouter(
                    ciudadGuardada = ciudadGuardada
                )
            }
        }
    }
}


