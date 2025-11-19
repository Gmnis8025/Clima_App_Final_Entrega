package com.example.clima_app_final.clima.presentation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clima_app_final.clima.domain.ClimaIntencion
import com.example.clima_app_final.clima.domain.PronosticoDia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimaPage(
    lat: Double,
    lon: Double,
    nombreCiudad: String,
    onCambiarCiudad: () -> Unit
) {
    val vm: ClimaViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val contexto = LocalContext.current

    // Cargar clima cuando cambia la ciudad
    LaunchedEffect(lat, lon, nombreCiudad) {
        vm.procesar(ClimaIntencion.CargarClima(lat, lon, nombreCiudad))
    }

    LaunchedEffect(estado.textoParaCompartir) {
        val texto = estado.textoParaCompartir
        if (texto != null) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, texto)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Compartir pronóstico")
            contexto.startActivity(shareIntent)
        }
    }

    // Colores BENDITOS
    val fondoArriba = Color(0xFF020617)
    val fondoAbajo = Color(0xFF0F172A)
    val azulBoton = Color(0xFF2563EB)
    val colorCardHoy = Color(0xFF111827)
    val colorCardDia = Color(0xFF1F2937)

    Scaffold(
        containerColor = fondoArriba
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(fondoArriba, fondoArriba, fondoAbajo)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // TÍTULO
            Text(
                text = "Clima - ${estado.ciudadNombre.ifBlank { nombreCiudad }}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )

            if (estado.cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            estado.error?.let { mensaje ->
                Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { vm.procesar(ClimaIntencion.LimpiarError) }) {
                    Text("Aceptar")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // CARD HOY
            estado.climaHoy?.let { climaHoy ->

                val desc = climaHoy.descripcion.lowercase()
                val emojiHoy = when {
                    "tormenta" in desc -> "⛈️"
                    "lluvia" in desc -> "🌧️"
                    "nieve" in desc -> "❄️"
                    "cielo claro" in desc || "despejado" in desc -> "☀️"
                    "nubes" in desc || "nublado" in desc -> "☁️"
                    "niebla" in desc || "neblina" in desc -> "🌫️"
                    else -> "🌦️"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorCardHoy,
                        contentColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Text(text = emojiHoy, fontSize = 34.sp)

                        Text(
                            text = "${"%.1f".format(climaHoy.temperaturaActual)}°C",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            text = climaHoy.descripcion,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = "Min ${"%.1f".format(climaHoy.temperaturaMin)}°C   Max ${"%.1f".format(climaHoy.temperaturaMax)}°C",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Humedad: ${climaHoy.humedad}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Próximos días
            Text(
                text = "Próximos días",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = Color.White,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(estado.pronostico) { dia ->
                    Card(
                        modifier = Modifier
                            .width(110.dp)
                            .height(135.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorCardDia,
                            contentColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = dia.dia, style = MaterialTheme.typography.titleSmall)
                            Text(text = dia.iconoEmoji, fontSize = 22.sp)
                            Text(
                                text = "Min ${"%.1f".format(dia.temperaturaMin)}°C",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "Max ${"%.1f".format(dia.temperaturaMax)}°C",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Gráfico
            Text(
                text = "Gráfico de temperaturas",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GraficoPronosticoGrande(estado.pronostico)

            Spacer(modifier = Modifier.height(32.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Button(
                    onClick = onCambiarCiudad,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azulBoton,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cambiar ciudad")
                }

                Button(
                    onClick = { vm.procesar(ClimaIntencion.PrepararTextoParaCompartir) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azulBoton,
                        contentColor = Color.White
                    )
                ) {
                    Text("Compartir")
                }
            }
        }
    }
}

// Gráfico grande final final final
@Composable
fun GraficoPronosticoGrande(pronostico: List<PronosticoDia>) {
    if (pronostico.isEmpty()) return

    val maxTemp = pronostico.maxOf { it.temperaturaMax }
    val minTemp = pronostico.minOf { it.temperaturaMin }
    val rango = (maxTemp - minTemp).coerceAtLeast(1.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        pronostico.forEach { dia ->
            val alturaRelativa = ((dia.temperaturaMax - minTemp) / rango).toFloat()
            val alturaBarra = (alturaRelativa * 100f).coerceAtLeast(30f)

            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(alturaBarra.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2563EB),
                                    Color(0xFF60A5FA)
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = dia.iconoEmoji, fontSize = 16.sp)
                Text(
                    text = dia.dia,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}
