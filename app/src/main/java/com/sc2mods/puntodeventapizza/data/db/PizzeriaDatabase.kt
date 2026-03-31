package com.sc2mods.puntodeventapizza.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sc2mods.puntodeventapizza.data.dao.RegistroVentaDao
import com.sc2mods.puntodeventapizza.data.dao.TurnoDao
import com.sc2mods.puntodeventapizza.data.entity.RegistroVenta
import com.sc2mods.puntodeventapizza.data.entity.Turno

// Aquí le decimos a Room cuáles son las tablas de nuestra base de datos
@Database(
    entities = [Turno::class, RegistroVenta::class],
    version = 2, // Si en el futuro agregas otra tabla, le cambias a version
    exportSchema = false
)
abstract class PizzeriaDatabase : RoomDatabase() {

    // Conectamos los DAOs para que la base de datos sepa qué consultas existen
    abstract fun turnoDao(): TurnoDao
    abstract fun registroVentaDao(): RegistroVentaDao

    // El bloque 'companion object' es para aplicar el patrón "Singleton"
    companion object {
        @Volatile
        private var INSTANCE: PizzeriaDatabase? = null

        fun getDatabase(context: Context): PizzeriaDatabase {
            // Si la conexión ya está abierta, la devuelve. Si no, crea una nueva.
            // Esto evita que abras 20 conexiones al mismo tiempo y se trabe la app.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PizzeriaDatabase::class.java,
                    "pizzeria_database" // El nombre de tu archivo SQLite real
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}