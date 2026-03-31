package com.sc2mods.puntodeventapizza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.sc2mods.puntodeventapizza.ui.CalculadoraScreen
import com.sc2mods.puntodeventapizza.ui.theme.PuntoDeVentaPizzaTheme
import com.sc2mods.puntodeventapizza.viewmodel.CajaViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sc2mods.puntodeventapizza.data.db.PizzeriaDatabase
import com.sc2mods.puntodeventapizza.ui.CorteCajaScreen
import com.sc2mods.puntodeventapizza.ui.InicioScreen
import com.sc2mods.puntodeventapizza.viewmodel.CajaViewModelFactory
import android.content.Context

// Definimos los nombres de nuestras pantallas
enum class Pantalla {
    INICIO, CALCULADORA, CORTE_CAJA
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantiene la pantalla encendida
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            PuntoDeVentaPizzaTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Obtenemos el contexto de la app
                    val context = LocalContext.current

                    // 2. Abrimos la conexión a SQLite (usando el Singleton que hicimos)
                    val database = PizzeriaDatabase.getDatabase(context)

                    // 3. Sacamos el DAO de ventas
                    val daoVentas = database.registroVentaDao()
                    val daoTurnos = database.turnoDao()

                    // --- UPDATE: CREAMOS EL ARCHIVO DE PREFERENCIAS AQUÍ ---
                    val sharedPreferences = context.getSharedPreferences("PreferenciasPizzeria", Context.MODE_PRIVATE)
                    // -------------------------------------------------------

                    // 4. Creamos el ViewModel usando nuestra Fábrica
                    val cajaViewModel: CajaViewModel = viewModel(
                        // --- UPDATE: LE PASAMOS EL SHARED PREFERENCES A LA FÁBRICA ---
                        factory = CajaViewModelFactory(daoVentas, daoTurnos, sharedPreferences)
                        // -------------------------------------------------------------
                    )

                    // Estado para saber en qué pantalla estamos (Iniciamos en INICIO)
                    var pantallaActual by remember { mutableStateOf(Pantalla.INICIO) }

                    // 5. EL PASO 5: El Enrutador usando el nuevo cerebro
                    when (pantallaActual) {
                        Pantalla.INICIO -> {
                            InicioScreen(
                                viewModel = cajaViewModel, // Le pasamos el cerebro
                                onTurnoIniciado = { fondo ->
                                    cajaViewModel.configurarFondo(fondo)
                                    pantallaActual = Pantalla.CALCULADORA
                                },
                                onContinuarTurno = {
                                    // Como el ViewModel ya recuperó la memoria al abrir la app,
                                    // aquí solo brincamos a la pantalla de la calculadora.
                                    pantallaActual = Pantalla.CALCULADORA
                                },
                                onIrACorte = {
                                    pantallaActual = Pantalla.CORTE_CAJA
                                }
                            )
                        }

                        Pantalla.CALCULADORA -> {
                            // Le pasamos el nuevo cerebro a tu calculadora
                            CalculadoraScreen(
                                viewModel = cajaViewModel,
                                onVolver = {
                                    pantallaActual = Pantalla.INICIO // Nos regresa al menú
                                },
                                onIrACorte = {
                                    pantallaActual = Pantalla.CORTE_CAJA
                                }
                            )
                        }

                        Pantalla.CORTE_CAJA -> {
                            CorteCajaScreen(
                                viewModel = cajaViewModel,
                                onVolver = {
                                    // Si se arrepiente, lo regresamos a la calculadora para que siga trabajando
                                    pantallaActual = Pantalla.CALCULADORA
                                },
                                onTurnoCerrado = {
                                    // Cuando el turno se cierra con éxito, vuelve al menú principal
                                    // Y como el turno ya es null, verá la "Cara A" de abrir nueva caja
                                    pantallaActual = Pantalla.INICIO
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}