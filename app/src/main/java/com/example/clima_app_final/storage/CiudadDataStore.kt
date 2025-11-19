package com.example.clima_app_final.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ciudad_prefs")

class CiudadDataStore(private val context: Context) {

    companion object {
        private val NOMBRE = stringPreferencesKey("nombre")
        private val PAIS = stringPreferencesKey("pais")
        private val LAT = doublePreferencesKey("lat")
        private val LON = doublePreferencesKey("lon")
    }

    val ciudadGuardada: Flow<CiudadGuardada?> =
        context.dataStore.data.map { prefs ->
            val nombre = prefs[NOMBRE]
            val pais = prefs[PAIS]
            val lat = prefs[LAT]
            val lon = prefs[LON]

            if (nombre != null && pais != null && lat != null && lon != null) {
                CiudadGuardada(nombre, pais, lat, lon)
            } else {
                null
            }
        }

    suspend fun guardarCiudad(nombre: String, pais: String, lat: Double, lon: Double) {
        context.dataStore.edit { prefs ->
            prefs[NOMBRE] = nombre
            prefs[PAIS] = pais
            prefs[LAT] = lat
            prefs[LON] = lon
        }
    }
}

data class CiudadGuardada(
    val nombre: String,
    val pais: String,
    val lat: Double,
    val lon: Double
)
