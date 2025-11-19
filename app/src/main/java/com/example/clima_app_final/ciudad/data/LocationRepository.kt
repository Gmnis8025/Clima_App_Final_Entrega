package com.example.clima_app_final.ciudad.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationRepository(private val context: Context) {

    private val fused by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    @SuppressLint("MissingPermission")
    suspend fun obtenerUltimaUbicacion(): Pair<Double, Double>? {
        return try {
            val loc = fused.lastLocation.await()
            if (loc != null) {
                Pair(loc.latitude, loc.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
