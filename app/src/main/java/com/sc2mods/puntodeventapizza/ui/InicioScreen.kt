package com.sc2mods.puntodeventapizza.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sc2mods.puntodeventapizza.viewmodel.CajaViewModel
// --- UPDATE: IMPORTAMOS LOS COLORES ---
import com.sc2mods.puntodeventapizza.ui.theme.NegroPizza
import com.sc2mods.puntodeventapizza.ui.theme.AmarilloMostaza
// --------------------------------------

@Composable
fun InicioScreen(
    viewModel: CajaViewModel,
    onTurnoIniciado: (Double) -> Unit,
    onContinuarTurno: () -> Unit,
    onIrACorte: () -> Unit
) {
    var showFondoDialog by remember { mutableStateOf(false) }
    var fondoInput by remember { mutableStateOf("") }
    val turnoActivo = viewModel.turnoAbierto

    var showMenuDialog by remember { mutableStateOf(false) }
    var in89 by remember { mutableStateOf("") }
    var in120 by remember { mutableStateOf("") }
    var in130 by remember { mutableStateOf("") }
    var in150 by remember { mutableStateOf("") }

    // --- UPDATE: ENVOLVEMOS TODO EN UN SCAFFOLD PARA FORZAR EL FONDO NEGRO ---
    Scaffold(
        containerColor = NegroPizza
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- UPDATE: TÍTULO EN AMARILLO ---
            Text(
                text = "PIZZERÍA SC2",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = AmarilloMostaza
            )

            Text(
                text = "By RP Studios",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray // Gris clarito para que se vea elegante
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (turnoActivo != null) {
                // CARA B: SÍ HAY UN TURNO EN CURSO
                Card(
                    // --- UPDATE: TARJETA OSCURA ---
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
                    modifier = Modifier.padding(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF424242))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("¡Tienes un turno en curso!", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text("Turno #${turnoActivo.idTurno}", color = Color.LightGray)
                        Text("Fondo Inicial: $${turnoActivo.fondoInicial}", color = Color.LightGray)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onContinuarTurno() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            // --- UPDATE: BOTÓN AMARILLO ---
                            colors = ButtonDefaults.buttonColors(containerColor = AmarilloMostaza, contentColor = Color.Black)
                        ) {
                            Text("Continuar Cobrando", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { onIrACorte() },
                            modifier = Modifier.fillMaxWidth(),
                            // --- UPDATE: BORDE AMARILLO ---
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmarilloMostaza)
                        ) {
                            Text("Cerrar Turno (Corte de Caja)", color = AmarilloMostaza)
                        }
                    }
                }
            } else {
                // CARA A: NO HAY NADA, ABRIR NUEVO TURNO
                Button(
                    onClick = { showFondoDialog = true },
                    modifier = Modifier.size(width = 250.dp, height = 60.dp),
                    // --- UPDATE: BOTÓN INICIAR EN AMARILLO ---
                    colors = ButtonDefaults.buttonColors(containerColor = AmarilloMostaza, contentColor = Color.Black)
                ) {
                    Text("Iniciar Turno", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        in89 = viewModel.precioP89.toString()
                        in120 = viewModel.precioP120.toString()
                        in130 = viewModel.precioP130.toString()
                        in150 = viewModel.precioP150.toString()
                        showMenuDialog = true
                    },
                    modifier = Modifier.size(width = 250.dp, height = 50.dp),
                    // --- UPDATE: BORDE AMARILLO ---
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmarilloMostaza)
                ) {
                    Text("Editar Precios del Menú", color = AmarilloMostaza)
                }
            }
        }
    }

    // --- DIÁLOGOS (Los dejamos con estilo estándar para que sean legibles) ---
    if (showFondoDialog) {
        AlertDialog(
            onDismissRequest = { showFondoDialog = false },
            title = { Text("Apertura de Caja") },
            text = {
                TextField(
                    value = fondoInput,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) fondoInput = it },
                    label = { Text("Efectivo inicial $") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    val montoFondo = fondoInput.toDoubleOrNull() ?: 0.0
                    showFondoDialog = false
                    onTurnoIniciado(montoFondo)
                }) {
                    Text("Abrir Caja")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFondoDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showMenuDialog) {
        AlertDialog(
            onDismissRequest = { showMenuDialog = false },
            title = { Text("Actualizar Precios", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Ingresa los nuevos precios para las pizzas base:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = in89,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) in89 = it },
                        label = { Text("Pizza de Peperoni") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = in120,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) in120 = it },
                        label = { Text("Peperoni Orilla Rellena") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = in130,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) in130 = it },
                        label = { Text("Pizza Clasica") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = in150,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) in150 = it },
                        label = { Text("Pizza Orilla Rellena") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p89 = in89.toDoubleOrNull() ?: 89.0
                    val p120 = in120.toDoubleOrNull() ?: 120.0
                    val p130 = in130.toDoubleOrNull() ?: 130.0
                    val p150 = in150.toDoubleOrNull() ?: 150.0

                    viewModel.actualizarPrecios(p89, p120, p130, p150)
                    showMenuDialog = false
                }) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        in89 = "89.0"
                        in120 = "120.0"
                        in130 = "130.0"
                        in150 = "150.0"
                    }) { Text("Restablecer", color = Color.Gray) }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = { showMenuDialog = false }) { Text("Cerrar") }
                }
            }
        )
    }
}