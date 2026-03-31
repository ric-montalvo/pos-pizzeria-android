package com.sc2mods.puntodeventapizza.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sc2mods.puntodeventapizza.viewmodel.CajaViewModel
// --- UPDATE: IMPORTAMOS NUESTROS COLORES OFICIALES ---
import com.sc2mods.puntodeventapizza.ui.theme.NegroPizza
import com.sc2mods.puntodeventapizza.ui.theme.AmarilloMostaza
import com.sc2mods.puntodeventapizza.ui.theme.RojoAcento
// ----------------------------------------------------
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorteCajaScreen(
    viewModel: CajaViewModel,
    onVolver: () -> Unit,
    onTurnoCerrado: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.calcularTotalesTurno()
    }

    val turno = viewModel.turnoAbierto
    val fondo = turno?.fondoInicial ?: 0.0
    val ventas = viewModel.totalVentasTurno
    val salidas = viewModel.totalSalidasTurno
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val totalEsperado = fondo + ventas - salidas

    var mostrarArticulos by remember { mutableStateOf(true) }
    var mostrarTickets by remember { mutableStateOf(true) }

    Scaffold(
        // --- UPDATE: FONDO DE LA PANTALLA EN NEGRO ---
        containerColor = NegroPizza,
        // ---------------------------------------------
        topBar = {
            TopAppBar(
                title = { Text("Corte de Caja", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // --- UPDATE: BARRA AMARILLO MOSTAZA ---
                    containerColor = AmarilloMostaza
                    // --------------------------------------
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- UPDATE: TEXTO EN BLANCO ---
            Text(
                text = "Resumen del Turno #${turno?.idTurno ?: "?"}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- UPDATE: TARJETA DEL TICKET GENERAL (ESTILO OSCURO) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF424242))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FilaResumen("Fondo Inicial:", fondo, textColor = Color.White)
                    FilaResumen("Ventas Totales (+):", ventas, textColor = Color(0xFF81C784)) // Verde suave
                    FilaResumen("Gastos/Salidas (-):", salidas, isSalida = true)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFF424242))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("EFECTIVO EN CAJÓN:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text(
                            "$${String.format("%.2f", totalEsperado)}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = AmarilloMostaza // Resaltamos el total final con el color de marca
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- TARJETA COLAPSABLE: ARTÍCULOS VENDIDOS ---
            if (viewModel.desgloseArticulos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarArticulos = !mostrarArticulos }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ARTÍCULOS VENDIDOS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                    Icon(
                        imageVector = if (mostrarArticulos) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimizar/Maximizar",
                        tint = AmarilloMostaza
                    )
                }

                if (mostrarArticulos) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF333333))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            viewModel.desgloseArticulos.forEach { articulo ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${articulo.second} x ${articulo.first}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                    Text("$${String.format("%.2f", articulo.third)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AmarilloMostaza)
                                }
                            }
                        }
                    }
                }
            }

            // --- LISTA COLAPSABLE: DESGLOSE DE TICKETS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarTickets = !mostrarTickets }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "DESGLOSE DE TICKETS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Icon(
                    imageVector = if (mostrarTickets) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Minimizar/Maximizar",
                    tint = AmarilloMostaza
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (mostrarTickets) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 4.dp)) {
                        items(viewModel.historialTurnoActual) { registro ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "[Folio ${registro.numeroTicket}] ${registro.concepto} ${registro.nota}",
                                    color = if (registro.esSalida) RojoAcento else Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${if(registro.esSalida) "-" else ""}$${String.format("%.2f", registro.monto)}",
                                    color = if (registro.esSalida) RojoAcento else Color(0xFF81C784),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF333333))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- UPDATE: BOTONES FINALES AJUSTADOS ---
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        val ticketGenerado = viewModel.obtenerTextoTicket()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, ticketGenerado)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Enviar Corte de Caja")
                        context.startActivity(shareIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // Borde amarillo para el botón de compartir
                border = androidx.compose.foundation.BorderStroke(1.dp, AmarilloMostaza)
            ) {
                Text("Compartir Resumen (WhatsApp / Texto)", color = AmarilloMostaza)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.cerrarTurnoActual()
                    onTurnoCerrado()
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                // Rojo sólido para el botón de cierre
                colors = ButtonDefaults.buttonColors(containerColor = RojoAcento)
            ) {
                Text("CONFIRMAR Y CERRAR TURNO", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
        }
    }
}

@Composable
fun FilaResumen(etiqueta: String, monto: Double, isSalida: Boolean = false, textColor: Color = Color.White) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge, color = textColor)
        Text(
            "$${String.format("%.2f", monto)}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSalida) RojoAcento else textColor
        )
    }
}