package com.sc2mods.puntodeventapizza.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sc2mods.puntodeventapizza.data.dao.RegistroVentaDao
import com.sc2mods.puntodeventapizza.data.entity.RegistroVenta
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.sc2mods.puntodeventapizza.data.dao.TurnoDao
import com.sc2mods.puntodeventapizza.data.entity.Turno

class CajaViewModel(
    private val registroVentaDao: RegistroVentaDao,
    private val turnoDao: TurnoDao,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    var precioP89 by mutableStateOf(sharedPrefs.getFloat("precio_89", 89f).toDouble())
        private set
    var precioP120 by mutableStateOf(sharedPrefs.getFloat("precio_120", 120f).toDouble())
        private set
    var precioP130 by mutableStateOf(sharedPrefs.getFloat("precio_130", 130f).toDouble())
        private set
    var precioP150 by mutableStateOf(sharedPrefs.getFloat("precio_150", 150f).toDouble())
        private set

    fun actualizarPrecios(p89: Double, p120: Double, p130: Double, p150: Double) {
        precioP89 = p89
        precioP120 = p120
        precioP130 = p130
        precioP150 = p150

        // Guardamos en el disco duro del celular para que no se borren al reiniciar
        sharedPrefs.edit().apply {
            putFloat("precio_89", p89.toFloat())
            putFloat("precio_120", p120.toFloat())
            putFloat("precio_130", p130.toFloat())
            putFloat("precio_150", p150.toFloat())
            apply()
        }
    }
    // ----------------------------------------------------------------------

    private var idTurnoActual: Long = 0L
    private val _listaRegistros = mutableStateListOf<RegistroVenta>()
    private var numeroTicketActual: Int = 1

    var totalVentasTurno by mutableStateOf(0.0)
        private set
    var totalSalidasTurno by mutableStateOf(0.0)
        private set
    var historialTurnoActual by mutableStateOf<List<RegistroVenta>>(emptyList())
        private set
    var desgloseArticulos by mutableStateOf<List<Triple<String, Int, Double>>>(emptyList())
        private set
    var turnoAbierto by mutableStateOf<Turno?>(null)
        private set

    init {
        cargarTurnoActivo()
    }

    private fun cargarTurnoActivo() {
        viewModelScope.launch {
            val turno = turnoDao.getTurnoAbierto()

            if (turno != null) {
                idTurnoActual = turno.idTurno
                turnoAbierto = turno
                val ventasDelDia = registroVentaDao.getRegistrosPorTurno(turno.idTurno)
                val ultimoTicket = ventasDelDia.maxOfOrNull { it.numeroTicket } ?: 0
                numeroTicketActual = ultimoTicket + 1
                Log.d("MI_PUNTO_DE_VENTA", "¡Memoria recuperada! Turno ID: ${turno.idTurno}")
            }
        }
    }

    var fondoDeCaja: Double = 0.0
        private set

    fun configurarFondo(fondo: Double) {
        viewModelScope.launch {
            val nuevoTurno = Turno(fondoInicial = fondo)
            idTurnoActual = turnoDao.insertarTurno(nuevoTurno)
            turnoAbierto = turnoDao.getTurnoAbierto()
        }
    }

    val listaRegistros: List<RegistroVenta> = _listaRegistros

    val totalPedido: Double
        get() = _listaRegistros.sumOf { if (it.esSalida) -it.monto else it.monto }

    fun agregarRegistro(concepto: String, monto: Double, esSalida: Boolean = false, nota : String = "" ) {
        if (idTurnoActual == 0L) return
        val nuevoRegistro = RegistroVenta(
            idTurnoFk = idTurnoActual,
            numeroTicket = numeroTicketActual,
            concepto = concepto,
            monto = monto,
            esSalida = esSalida,
            nota = nota
        )
        _listaRegistros.add(nuevoRegistro)
    }

    fun borrarUltimo() {
        if (_listaRegistros.isNotEmpty()) {
            val ultimoRegistro = _listaRegistros.last()
            _listaRegistros.remove(ultimoRegistro)
        }
    }

    fun finalizarPedido() {
        if (_listaRegistros.isEmpty()) return
        val ticketConfirmado = _listaRegistros.toList()
        viewModelScope.launch {
            ticketConfirmado.forEach { registro ->
                registroVentaDao.insertarRegistro(registro)
            }
            numeroTicketActual++
            _listaRegistros.clear()
        }
    }

    fun cerrarTurnoActual() {
        val turnoActual = turnoAbierto ?: return
        viewModelScope.launch {
            val turnoCerrado = turnoActual.copy(estado = "CERRADO")
            turnoDao.actualizarTurno(turnoCerrado)
            idTurnoActual = 0L
            turnoAbierto = null
            numeroTicketActual = 1
            _listaRegistros.clear()
        }
    }

    fun calcularTotalesTurno() {
        if (idTurnoActual == 0L) return
        viewModelScope.launch {
            val registrosDelDia = registroVentaDao.getRegistrosPorTurno(idTurnoActual)
            historialTurnoActual = registrosDelDia
            val ventas = registrosDelDia.filter { !it.esSalida }
            totalVentasTurno = ventas.sumOf { it.monto }
            totalSalidasTurno = registrosDelDia.filter { it.esSalida }.sumOf { it.monto }
            desgloseArticulos = ventas.groupBy { it.concepto }
                .map { (concepto, lista) ->
                    Triple(concepto, lista.size, lista.sumOf { it.monto })
                }.sortedByDescending { it.second }
        }
    }

    suspend fun obtenerTextoTicket(): String {
        val idTurno = idTurnoActual
        if (idTurno == 0L) return "Error: No hay turno activo"

        val registros = registroVentaDao.getRegistrosPorTurno(idTurno)
        val ventas = registros.filter { !it.esSalida }
        val salidas = registros.filter { it.esSalida }

        val fondo = turnoAbierto?.fondoInicial ?: 0.0
        val totalVendido = ventas.sumOf { it.monto }
        val totalGastado = salidas.sumOf { it.monto }
        val efectivoCajon = fondo + totalVendido - totalGastado

        var ticket = "🍕 CORTE DE CAJA - PIZZERÍA SC2 🍕\n"
        ticket += "Hecho por Ricardo Montalvo\n"
        ticket += "--------------------------------\n"
        ticket += "Turno #: $idTurno\n"
        ticket += "Fondo Inicial: $${String.format("%.2f", fondo)}\n"
        ticket += "--------------------------------\n"

        if (ventas.isNotEmpty()) {
            ticket += "📦 ARTÍCULOS VENDIDOS:\n"
            val agrupado = ventas.groupBy { it.concepto }
            agrupado.forEach { (concepto, lista) ->
                val cant = lista.size
                val sum = lista.sumOf { it.monto }
                ticket += "$cant x $concepto =$${String.format("%.2f", sum)}\n"
            }
            ticket += "--------------------------------\n"

            ticket += "📋 DETALLE DE TICKETS:\n"
            ventas.forEach {
                ticket += "[Folio ${it.numeroTicket}] ${it.concepto}: $${it.monto}\n"
            }
            ticket += "--------------------------------\n"
        }

        if (salidas.isNotEmpty()) {
            ticket += "📉 SALIDAS:\n"
            salidas.forEach {
                ticket += "${it.concepto} (${it.nota}): -$${it.monto}\n"
            }
            ticket += "--------------------------------\n"
        }

        ticket += "💰 RESUMEN FINAL:\n"
        ticket += "Total Ventas: +$${String.format("%.2f", totalVendido)}\n"
        ticket += "Total Salidas: -$${String.format("%.2f", totalGastado)}\n"
        ticket += "EFECTIVO ESPERADO:$${String.format("%.2f", efectivoCajon)}\n"
        ticket += "--------------------------------\n"
        ticket += "Generado desde Punto de Venta SC2"

        return ticket
    }
}

class CajaViewModelFactory(
    private val ventaDao: RegistroVentaDao,
    private val turnoDao: TurnoDao,
    private val sharedPrefs: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CajaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CajaViewModel(ventaDao, turnoDao, sharedPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}