package com.sc2mods.puntodeventapizza.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sc2mods.puntodeventapizza.data.entity.Turno

@Dao
interface TurnoDao {
    // Inserta un turno y nos devuelve el ID que se autogeneró (1, 2, 3...)
    @Insert
    suspend fun insertarTurno(turno: Turno): Long

    // Lo usaremos para el Corte de Caja (cambiar estado a CERRADO)
    @Update
    suspend fun actualizarTurno(turno: Turno)

    // Busca si hay un turno abierto para no pedir el fondo de caja otra vez si se cierra la app
    @Query("SELECT * FROM turnos WHERE estado = 'ABIERTO' ORDER BY idTurno DESC LIMIT 1")
    suspend fun getTurnoAbierto(): Turno?


}