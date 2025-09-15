package com.example.accesibilidad.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
// Modos y preferencias
// ─────────────────────────────────────────────
enum class AppThemeMode { System, Light, Dark }
enum class TextSizePref { Small, Medium, Large }

// CompositionLocal para que, si lo necesitas, puedas leer la escala en las pantallas
val LocalTextScale = staticCompositionLocalOf { 1.0f }

// Usa la tipografía que ya tienes en Type.kt
private val BaseTypography = Typography

private fun scaledTypography(scale: Float): Typography = Typography(
    displayLarge  = BaseTypography.displayLarge.copy(fontSize = (BaseTypography.displayLarge.fontSize.value * scale).sp),
    displayMedium = BaseTypography.displayMedium.copy(fontSize = (BaseTypography.displayMedium.fontSize.value * scale).sp),
    displaySmall  = BaseTypography.displaySmall.copy(fontSize = (BaseTypography.displaySmall.fontSize.value * scale).sp),
    headlineLarge = BaseTypography.headlineLarge.copy(fontSize = (BaseTypography.headlineLarge.fontSize.value * scale).sp),
    headlineMedium= BaseTypography.headlineMedium.copy(fontSize = (BaseTypography.headlineMedium.fontSize.value * scale).sp),
    headlineSmall = BaseTypography.headlineSmall.copy(fontSize = (BaseTypography.headlineSmall.fontSize.value * scale).sp),
    titleLarge    = BaseTypography.titleLarge.copy(fontSize = (BaseTypography.titleLarge.fontSize.value * scale).sp),
    titleMedium   = BaseTypography.titleMedium.copy(fontSize = (BaseTypography.titleMedium.fontSize.value * scale).sp),
    titleSmall    = BaseTypography.titleSmall.copy(fontSize = (BaseTypography.titleSmall.fontSize.value * scale).sp),
    bodyLarge     = BaseTypography.bodyLarge.copy(fontSize = (BaseTypography.bodyLarge.fontSize.value * scale).sp),
    bodyMedium    = BaseTypography.bodyMedium.copy(fontSize = (BaseTypography.bodyMedium.fontSize.value * scale).sp),
    bodySmall     = BaseTypography.bodySmall.copy(fontSize = (BaseTypography.bodySmall.fontSize.value * scale).sp),
    labelLarge    = BaseTypography.labelLarge.copy(fontSize = (BaseTypography.labelLarge.fontSize.value * scale).sp),
    labelMedium   = BaseTypography.labelMedium.copy(fontSize = (BaseTypography.labelMedium.fontSize.value * scale).sp),
    labelSmall    = BaseTypography.labelSmall.copy(fontSize = (BaseTypography.labelSmall.fontSize.value * scale).sp),
)

// ─────────────────────────────────────────────
// THEME CENTRAL
// ─────────────────────────────────────────────
@Composable
fun AppTheme(
    themeMode: AppThemeMode,
    highContrast: Boolean,
    textSizePref: TextSizePref,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.System -> isSystemInDarkTheme()
        AppThemeMode.Dark   -> true
        AppThemeMode.Light  -> false
    }

    // Paletas definidas en Color.kt
    val colors = when {
        highContrast && isDark  -> HighContrastDarkColors
        highContrast && !isDark -> HighContrastLightColors
        !highContrast && isDark -> NormalDarkColors
        else                    -> NormalLightColors
    }

    val scale = when (textSizePref) {
        TextSizePref.Small  -> 0.90f
        TextSizePref.Medium -> 1.00f
        TextSizePref.Large  -> 1.15f
    }
    val typography = scaledTypography(scale)

    CompositionLocalProvider(LocalTextScale provides scale) {
        MaterialTheme(
            colorScheme = colors,
            typography  = typography,
            content     = content
        )
    }
}

// ─────────────────────────────────────────────
// Wrapper para compatibilidad con código existente
// (si en algún lugar llamabas AccesibilidadTheme)
// ─────────────────────────────────────────────
@Composable
fun AccesibilidadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // ignoramos dynamicColor para mantener alto contraste estable
    content: @Composable () -> Unit
) {
    val mode = if (darkTheme) AppThemeMode.Dark else AppThemeMode.Light
    AppTheme(
        themeMode = mode,
        highContrast = false,
        textSizePref = TextSizePref.Medium,
        content = content
    )
}
