package com.example.accesibilidad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.accesibilidad.accessibility.rememberTtsController
import com.example.accesibilidad.nav.AppNavHost
import com.example.accesibilidad.ui.theme.AppTheme
import com.example.accesibilidad.ui.theme.AppThemeMode
import com.example.accesibilidad.ui.theme.TextSizePref

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estados de preferencias (persisten en la sesión)
            var themeMode    by rememberSaveable { mutableStateOf(AppThemeMode.System) }
            var highContrast by rememberSaveable { mutableStateOf(false) }
            var textSizePref by rememberSaveable { mutableStateOf(TextSizePref.Medium) }
            var ttsEnabled   by rememberSaveable { mutableStateOf(false) }

            // Controlador TTS y NavController
            val navController = rememberNavController()
            val ttsController = rememberTtsController()

            AppTheme(
                themeMode = themeMode,
                highContrast = highContrast,
                textSizePref = textSizePref
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController,
                        // Preferencias y setters (para PreferencesScreen)
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it },
                        highContrast = highContrast,
                        onHighContrastChange = { highContrast = it },
                        textSizePref = textSizePref,
                        onTextSizeChange = { textSizePref = it },
                        // TTS
                        ttsEnabled = ttsEnabled,
                        onTtsChange = { ttsEnabled = it },
                        ttsController = ttsController
                    )
                }
            }
        }
    }
}
