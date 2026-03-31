package com.sc2mods.puntodeventapizza.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sc2mods.puntodeventapizza.data.entity.RegistroVenta

@Dao
interface RegistroVentaDao {
    // Guarda una pizza o una salida de gas
    @Insert
    suspend fun insertarRegistro(registro: RegistroVenta)

    // Nos traerá todas las ventas de un turno específico para hacer la suma del Corte de Caja
    @Query("SELECT * FROM registro_ventas WHERE idTurnoFk = :idTurno")
    suspend fun getRegistrosPorTurno(idTurno: Long): List<RegistroVenta>

    @Delete
    suspend fun borrarRegistro(registro: RegistroVenta)
}