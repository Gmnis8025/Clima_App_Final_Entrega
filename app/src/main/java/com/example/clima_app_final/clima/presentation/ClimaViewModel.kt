package com.example.clima_app_final.clima.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clima_app_final.clima.data.ClimaRepository
import com.example.clima_app_final.clima.data.ClimaRepositoryApi
import com.example.clima_app_final.clima.domain.ClimaEstado
import com.example.clima_app_final.clima.domain.ClimaIntencion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClimaViewModel(
    private val repository: ClimaRepository = ClimaRepositoryApi()
) : ViewModel() {

    private val _estado = MutableStateFlow(ClimaEstado())
    val estado: StateFlow<ClimaEstado> = _estado

    fun procesar(intencion: ClimaIntencion) {
        when (intencion) {
            is ClimaIntencion.CargarClima -> cargarClima(
                intencion.lat,
                intencion.lon,
                intencion.nombreCiudad
            )

            ClimaIntencion.Refrescar -> refrescar()
            ClimaIntencion.PrepararTextoParaCompartir -> prepararTextoParaCompartir()
            ClimaIntencion.LimpiarError -> {
                _estado.update { it.copy(error = null) }
            }
        }
    }

    private fun cargarClima(lat: Double, lon: Double, nombreCiudad: String) {
        _estado.update {
            it.copy(
                cargando = true,
                ciudadNombre = nombreCiudad,
                lat = lat,
                lon = lon,
                error = null,
                textoParaCompartir = null
            )
        }

        viewModelScope.launch {
            try {
                val hoy = repository.obtenerClimaHoy(lat, lon)
                val pronostico = repository.obtenerPronostico5Dias(lat, lon)

                _estado.update {
                    it.copy(
                        cargando = false,
                        climaHoy = hoy,
                        pronostico = pronostico
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        cargando = false,
                        error = "Error al obtener el clima: ${e.message}"
                    )
                }
            }
        }
    }

    private fun refrescar() {
        val estadoActual = _estado.value
        // Reutilizo valores ya guardados
        cargarClima(
            estadoActual.lat,
            estadoActual.lon,
            estadoActual.ciudadNombre
        )
    }

    private fun prepararTextoParaCompartir() {
        val e = _estado.value
        val hoy = e.climaHoy ?: return

        val texto = buildString {
            appendLine("Clima en ${e.ciudadNombre}")
            appendLine("Hoy: ${hoy.descripcion}")
            appendLine("Temp actual: ${hoy.temperaturaActual}°C")
            appendLine("Mín: ${hoy.temperaturaMin}°C  Máx: ${hoy.temperaturaMax}°C")
            appendLine("Humedad: ${hoy.humedad}%")
        }

        _estado.update {
            it.copy(textoParaCompartir = texto)
        }
    }
}