package com.example.clima_app_final.ciudad.presentation

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clima_app_final.ciudad.data.CiudadesRepositoryApi
import com.example.clima_app_final.ciudad.data.LocationRepository
import com.example.clima_app_final.ciudad.domain.Ciudad
import com.example.clima_app_final.ciudad.domain.CiudadesIntencion
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun CiudadesPage(
    onSeleccionarCiudad: (Ciudad) -> Unit
) {
    val vm: CiudadesViewModel = viewModel()
    val estado by vm.estado.collectAsState()

    val fondoArriba = Color(0xFF020617)
    val fondoAbajo = Color(0xFF0F172A)
    val azulBoton = Color(0xFF2563EB)


    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()


    val locationRepository = remember { LocationRepository(contexto) }
    val ciudadesApi = remember { CiudadesRepositoryApi() }


    val pedirPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            scope.launch {
                val ubic = locationRepository.obtenerUltimaUbicacion()
                if (ubic != null) {
                    val (lat, lon) = ubic

                    val ciudad = ciudadesApi.obtenerCiudadPorGeolocalizacion(lat, lon)
                    if (ciudad != null) {

                        onSeleccionarCiudad(ciudad)
                    } else {
                        vm.procesar(CiudadesIntencion.LimpiarError)
                    }
                } else {

                    vm.procesar(CiudadesIntencion.LimpiarError)
                }
            }
        } else {

        }
    }

    LaunchedEffect(Unit) {
        vm.procesar(CiudadesIntencion.CargarInicial)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(fondoArriba, fondoArriba, fondoAbajo)
                )
            )
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        // TÍTULO
        Text(
            text = "Seleccionar ciudad",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 22.dp)
        )

        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = { texto ->
                vm.procesar(CiudadesIntencion.BuscarPorTexto(texto))
            },
            placeholder = {
                Text("Buscar ciudad", color = Color.White.copy(alpha = 0.6f))
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulBoton,
                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        //  BOTÓN USAR MI UBICACIÓN SUFRIMIENTO
        Button(
            onClick = {
                pedirPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = buttonColors(
                containerColor = azulBoton,
                contentColor = Color.White
            )
        ) {
            Text("Usar mi ubicación")
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (estado.cargando) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
            return@Column
        }

        estado.error?.let { mensaje ->
            Text(text = mensaje, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { vm.procesar(CiudadesIntencion.LimpiarError) },
                colors = buttonColors(containerColor = azulBoton)
            ) {
                Text("Aceptar", color = Color.White)
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(estado.ciudades) { ciudad ->
                CiudadItem(
                    ciudad = ciudad,
                    onClick = { onSeleccionarCiudad(ciudad) }
                )
            }
        }
    }
}

@Composable
fun CiudadItem(
    ciudad: Ciudad,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B),
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${ciudad.nombre}, ${ciudad.pais}",
                style = MaterialTheme.typography.titleMedium
            )

        }
    }
}
