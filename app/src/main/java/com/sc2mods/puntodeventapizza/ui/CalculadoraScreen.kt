package com.sc2mods.puntodeventapizza.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// --- UPDATE: IMPORTAMOS NUESTROS COLORES OFICIALES ---
import com.sc2mods.puntodeventapizza.ui.theme.NegroPizza
import com.sc2mods.puntodeventapizza.ui.theme.AmarilloMostaza
import com.sc2mods.puntodeventapizza.ui.theme.RojoAcento
// ----------------------------------------------------
import com.sc2mods.puntodeventapizza.viewmodel.CajaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraScreen(
    viewModel: CajaViewModel = viewModel(),
    onVolver: () -> Unit = {},
    onIrACorte: () -> Unit = {}
) {
    // Estados para la ventana de Custom
    var showDialog by remember { mutableStateOf(false) }
    var customPrice by remember { mutableStateOf("") }

    // Para salida
    var showSalidaDialog by remember { mutableStateOf(false) }
    var salidaMonto by remember { mutableStateOf("") }
    var salidaNota by remember { mutableStateOf("") }

    Scaffold(
        // --- UPDATE: FONDO DE LA PANTALLA EN NEGRO CARBÓN ---
        containerColor = NegroPizza,
        // ---------------------------------------------------
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Caja Activa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        // --- UPDATE: LETRAS NEGRAS SOBRE FONDO AMARILLO ---
                        color = Color.Black
                        // --------------------------------------------------
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onVolver() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            // --- UPDATE: ÍCONO NEGRO ---
                            tint = Color.Black
                            // ---------------------------
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onIrACorte() }) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Ir a Corte Rápido",
                            // --- UPDATE: ÍCONO NEGRO ---
                            tint = Color.Black
                            // ---------------------------
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // --- UPDATE: BARRA SUPERIOR AMARILLO MOSTAZA (Contraste Alto) ---
                    containerColor = AmarilloMostaza
                    // ----------------------------------------------------------------
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            // --- FILA SUPERIOR: VISTA Y DELETE ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- UPDATE: TEXTO TITULO EN BLANCO ---
                Text(
                    text = "VENTA ACTUAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                // --------------------------------------

                TextButton(onClick = { viewModel.borrarUltimo() }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Borrar",
                        // --- UPDATE: ROJO MÁS INTENSO ---
                        tint = RojoAcento
                        // --------------------------------
                    )
                    Text(
                        "Borrar último",
                        // --- UPDATE: ROJO MÁS INTENSO Y NEGRILLA ---
                        color = RojoAcento,
                        fontWeight = FontWeight.Bold
                        // --------------------------------------------
                    )
                }
            }

            // --- DISPLAY ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 4.dp),
                // --- UPDATE: FONDO DEL DISPLAY OSCURO ---
                colors = CardDefaults.cardColors(containerColor = Color(0xFF262626))
                // ----------------------------------------
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = "$ ${String.format("%.2f", viewModel.totalPedido)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        // --- UPDATE: NÚMEROS EN BLANCO PURO ---
                        color = Color.White,
                        // ---------------------------------------
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            // --- UPDATE: FILA 1 DE PIZZAS (Vuelven a FONDO AMARILLO para identidad visual) ---
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                BotonBase(
                    "${viewModel.precioP89.toInt()}",
                    Modifier.weight(1f),
                    // Cambiamos el fondo a Amarillo Mostaza
                    containerColor = AmarilloMostaza,
                    // Letras Negras para máximo contraste
                    contentColor = Color.Black
                ) {
                    viewModel.agregarRegistro("Pizza ${viewModel.precioP89.toInt()}", viewModel.precioP89)
                }

                BotonBase(
                    "${viewModel.precioP120.toInt()}",
                    Modifier.weight(1f),
                    containerColor = AmarilloMostaza,
                    contentColor = Color.Black
                ) {
                    viewModel.agregarRegistro("Pizza ${viewModel.precioP120.toInt()}", viewModel.precioP120)
                }

                BotonBase(
                    "${viewModel.precioP130.toInt()}",
                    Modifier.weight(1f),
                    containerColor = AmarilloMostaza,
                    contentColor = Color.Black
                ) {
                    viewModel.agregarRegistro("Pizza ${viewModel.precioP130.toInt()}", viewModel.precioP130)
                }

                BotonBase(
                    "Orilla\n${viewModel.precioP150.toInt()}",
                    Modifier.weight(1f),
                    containerColor = AmarilloMostaza,
                    contentColor = Color.Black
                ) {
                    viewModel.agregarRegistro("Pizza Orilla ${viewModel.precioP150.toInt()}", viewModel.precioP150)
                }
            }
            // --------------------------------------------------------------------------------

            // --- UPDATE: FILA 2 Y 3: EXTRAS (Mismo estilo que Pizzas) ---
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                BotonBase(
                    "Chimi $15",
                    Modifier.weight(1f),
                    containerColor = Color.Black,
                    contentColor = AmarilloMostaza
                ) {
                    viewModel.agregarRegistro("Chimichurri", 15.0)
                }
                BotonBase(
                    "Ing. Xtra $10",
                    Modifier.weight(1f),
                    containerColor = Color.Black,
                    contentColor = AmarilloMostaza
                ) {
                    viewModel.agregarRegistro("Ing xtra", 10.0)
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                BotonBase(
                    "Habanera $28",
                    Modifier.weight(1f),
                    containerColor = Color.Black,
                    contentColor = AmarilloMostaza
                ) {
                    viewModel.agregarRegistro("Salsa Habanera", 28.0)
                }
                BotonBase(
                    "Queso Xtra $30",
                    Modifier.weight(1f),
                    containerColor = Color.Black,
                    contentColor = AmarilloMostaza
                ) {
                    viewModel.agregarRegistro("Q. Xtra", 30.0)
                }
            }
            // ------------------------------------------------------------

            Spacer(modifier = Modifier.height(8.dp))

            /// --- BLOQUE FINAL: ACCIONES GRANDES ---
            Column(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    // --- UPDATE: BOTÓN SALIDA (Rojo profundo, muy llamativo sobre negro) ---
                    BotonBase(
                        "SALIDA",
                        Modifier.weight(1f),
                        containerColor = Color(0xFFD32F2F), // Rojo Intenso
                        contentColor = Color.White
                    ) {
                        showSalidaDialog = true
                    }
                    // -----------------------------------------------------------------------

                    // --- UPDATE: BOTÓN CONFIRMAR (Verde profundo, muy llamativo) ---
                    BotonBase(
                        "CONFIRMAR",
                        Modifier.weight(1f),
                        containerColor = Color(0xFF388E3C), // Verde Intenso
                        contentColor = Color.White
                    ) {
                        viewModel.finalizarPedido()
                    }
                    // ---------------------------------------------------------------
                }
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    // --- UPDATE: BOTÓN CUSTOM (Gris oscuro para que no compita) ---
                    BotonBase(
                        "CUSTOM",
                        Modifier.weight(1f),
                        containerColor = Color(0xFF424242), // Gris Oscuro
                        contentColor = AmarilloMostaza
                    ){
                        showDialog = true
                    }
                    // ---------------------------------------------------------------
                }
            }

            // --- 2. LA LIBRETA (HISTORIAL) ---
            Spacer(modifier = Modifier.height(8.dp))
            // --- UPDATE: TEXTO EN BLANCO ---
            Text(
                "HISTORIAL DE LA VENTA ACTUAL",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            // -------------------------------

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(viewModel.listaRegistros) { registro ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // --- UPDATE: TEXTOS DE LA LISTA EN BLANCO/GRIS ---
                        Text(
                            text = "${if (registro.esSalida) "[-] " else ""}${registro.concepto} ${registro.nota}",
                            color = if (registro.esSalida) RojoAcento else Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.2f", registro.monto)}",
                            // Usamos un verde lima clarito para que resalte el dinero cobrado
                            color = if (registro.esSalida) RojoAcento else Color(0xFF81C784),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        // --------------------------------------------------
                    }
                    // --- UPDATE: DIVISOR OSCURO ---
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF424242))
                    // ------------------------------
                }
            }
        }
    }

    // --- DIÁLOGOS (No cambiamos colores aquí para usar los del sistema) ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cobro Personalizado", fontWeight = FontWeight.Bold) },
            text = {
                TextField(
                    value = customPrice,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) customPrice = it },
                    label = { Text("Precio $") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    val precio = customPrice.toDoubleOrNull() ?: 0.0
                    if (precio > 0) viewModel.agregarRegistro("CUSTOM", precio)
                    showDialog = false
                    customPrice = ""
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showSalidaDialog) {
        AlertDialog(
            onDismissRequest = { showSalidaDialog = false },
            title = { Text("Registrar Salida de Dinero", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    TextField(
                        value = salidaMonto,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) salidaMonto = it },
                        label = { Text("Monto $") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = salidaNota,
                        onValueChange = { salidaNota = it },
                        label = { Text("Motivo (Ej. Gas, Insumos)") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val monto = salidaMonto.toDoubleOrNull() ?: 0.0
                    if (monto > 0) viewModel.agregarRegistro(concepto = "SALIDA", monto = monto, esSalida = true, nota = salidaNota)
                    showSalidaDialog = false
                    salidaMonto = ""
                    salidaNota = ""
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showSalidaDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun BotonBase(
    texto: String,
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        // --- UPDATE: AGREGAMOS BORDE AMARILLO A LOS BOTONES NEGROS PARA QUE RESALTEN ---
        border = if (containerColor == Color.Black) androidx.compose.foundation.BorderStroke(1.dp, AmarilloMostaza) else null
        // -------------------------------------------------------------------------------
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}