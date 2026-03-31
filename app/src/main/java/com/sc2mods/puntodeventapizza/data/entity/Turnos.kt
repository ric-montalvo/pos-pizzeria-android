package com.sc2mods.puntodeventapizza.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity le dice a Room: "Crea una tabla en SQLite llamada 'turnos' con esto"
@Entity(tableName = "turnos")
data class Turno(
    @PrimaryKey(autoGenerate = true) // El ID se suma solo
    val idTurno: Long = 0,
    val fondoInicial: Double,
    val fechaApertura: Long = System.currentTimeMillis(), // Guardamos la hora exacta en milisegundos
    val fechaCierre: Long? = null, // Puede ser null porque al abrir, aún no cerramos
    val estado: String = "ABIERTO" // Puede ser "ABIERTO" o "CERRADO"
)