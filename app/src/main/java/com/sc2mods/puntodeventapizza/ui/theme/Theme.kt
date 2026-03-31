package com.sc2mods.puntodeventapizza.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// --- UPDATE: MAPEO DE COLORES PARA MODO OSCURO (Para cuando quieras activarlo) ---
private val DarkColorScheme = darkColorScheme(
    primary = AmarilloMostaza,
    secondary = NegroPizza,
    tertiary = RojoAcento,
    background = NegroPizza,
    surface = NegroPizza
)

// --- UPDATE: MAPEO DE COLORES PARA MODO CLARO (El que estamos usando) ---
private val LightColorScheme = lightColorScheme(
    primary = NegroPizza,          // Color de las barras superiores y botones principales
    secondary = AmarilloMostaza,   // Color para acentos
    tertiary = RojoAcento,         // Color para alertas/destrucción
    background = FondoGrisClaro,   // Fondo general de las pantallas
    surface = BlancoPuro           // Fondo de las tarjetas (Cards)
)

@Composable
fun PuntoDeVentaPizzaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // --- UPDATE: APAGAMOS EL COLOR DINÁMICO ---
    // En 'false' forzamos a que TODOS los celulares respeten nuestros colores oficiales.
    dynamicColor: Boolean = false,
    // ------------------------------------------
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asume que tienes el archivo Type.kt, lo dejamos igual
        content = content
    )
}