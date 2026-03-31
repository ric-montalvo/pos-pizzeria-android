package com.sc2mods.puntodeventapizza.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(//entidad que pertenece a un turno por medio de la FK
    tableName = "registro_ventas",
    foreignKeys = [
        ForeignKey(
            entity = Turno::class,
            parentColumns = ["idTurno"],
            childColumns = ["idTurnoFk"],
            onDelete = ForeignKey.CASCADE // Si borras un turno, se borran sus ventas
        )
    ]
)

data class RegistroVenta (
    @PrimaryKey(autoGenerate = true)
    val idRegistro: Long = 0,
    val idTurnoFk: Long,
    val numeroTicket: Int,
    val concepto : String,
    val monto : Double,
    val esSalida : Boolean = false,
    val nota: String = ""
)