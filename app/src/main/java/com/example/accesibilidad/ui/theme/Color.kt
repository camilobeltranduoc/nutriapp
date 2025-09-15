package com.example.accesibilidad.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


// ─────────────────────────────────────────────
// Paletas "Normales" (accesibles)
// ─────────────────────────────────────────────
val NormalLightColors = lightColorScheme(
    primary = Color(0xFF1565C0),     // Azul accesible (botones/links)
    onPrimary = Color.White,
    secondary = Color(0xFF2E7D32),   // Verde accesible (confirmación)
    onSecondary = Color.White,
    error = Color(0xFFB00020),       // Rojo accesible (errores)
    onError = Color.White,
    background = Color(0xFFFDFDFD),
    onBackground = Color(0xFF111111),
    surface = Color.White,
    onSurface = Color(0xFF111111)
)

val NormalDarkColors = darkColorScheme()
// Si quieres forzar un primario específico en dark:
// val NormalDarkColors = darkColorScheme(primary = Color(0xFF90CAF9))

// ─────────────────────────────────────────────
// Paletas de ALTO CONTRASTE (mejor WCAG)
// ─────────────────────────────────────────────
val HighContrastLightColors = lightColorScheme(
    primary = Color(0xFF0D47A1),     // más oscuro → mayor contraste
    onPrimary = Color.White,
    secondary = Color(0xFF1B5E20),
    onSecondary = Color.White,
    error = Color(0xFFB00020),
    onError = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0A0A0A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0A0A0A)
)

val HighContrastDarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0A0A0A),
    secondary = Color(0xFFA5D6A7),
    onSecondary = Color(0xFF0A0A0A),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF0A0A0A),
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFF5F5F5)
)