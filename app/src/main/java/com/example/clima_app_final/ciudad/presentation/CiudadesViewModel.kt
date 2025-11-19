package com.example.clima_app_final.ciudad.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clima_app_final.ciudad.data.CiudadesRepository
import com.example.clima_app_final.ciudad.data.CiudadesRepositoryApi
import com.example.clima_app_final.ciudad.domain.CiudadesEstado
import com.example.clima_app_final.ciudad.domain.CiudadesIntencion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CiudadesViewModel(
    private val repository: CiudadesRepository = CiudadesRepositoryApi()
) : ViewModel() {

    private val _estado = MutableStateFlow(CiudadesEstado())
    val estado: StateFlow<CiudadesEstado> = _estado

    fun procesar(intencion: CiudadesIntencion) {
        when (intencion) {
            is CiudadesIntencion.CargarInicial -> cargarCiudadesIniciales()
            is CiudadesIntencion.BuscarPorTexto -> buscarPorTexto(intencion.texto)
            is CiudadesIntencion.BuscarPorUbicacion -> buscarPorGeo()

            is CiudadesIntencion.SeleccionarCiudad -> {
            }

            is CiudadesIntencion.LimpiarError -> {
                _estado.update { it.copy(error = null) }
            }

            is CiudadesIntencion.MostrarError -> {
                _estado.value = _estado.value.copy(
                    error = intencion.mensaje,
                    cargando = false
                )
            }
        }
    }

    private fun cargarCiudadesIniciales() {
        _estado.update {
            it.copy(
                cargando = false,
                error = null,
                ciudades = emptyList(),
                busqueda = ""
            )
        }
    }

    private fun buscarPorTexto(texto: String) {
        // Actualizamos el texto del buscador
        _estado.update { it.copy(busqueda = texto) }

        // Si está vacío  limpiamos lista y no llamamos a la API
        if (texto.isBlank()) {
            _estado.update {
                it.copy(
                    ciudades = emptyList(),
                    cargando = false,
                    error = null
                )
            }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            try {
                val ciudades = repository.buscarCiudadesPorNombre(texto)
                _estado.update {
                    it.copy(
                        cargando = false,
                        ciudades = ciudades
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        cargando = false,
                        error = "Error al buscar ciudades: ${e.message}"
                    )
                }
            }
        }
    }

    //prueba
    private fun buscarPorGeo() {
        val latEjemplo = -34.6090
        val lonEjemplo = -68.3816

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }

            try {
                val ciudad = repository.obtenerCiudadPorGeolocalizacion(
                    latEjemplo,
                    lonEjemplo
                )

                ciudad?.let {
                    _estado.update { estadoActual ->
                        estadoActual.copy(
                            cargando = false,
                            ciudades = listOf(it),
                            busqueda = it.nombre
                        )
                    }
                } ?: run {
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = "No se pudo obtener ciudad por ubicación"
                        )
                    }
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        cargando = false,
                        error = "Error al obtener ubicación: ${e.message}"
                    )
                }
            }
        }
    }
}
