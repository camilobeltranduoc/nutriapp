package com.example.accesibilidad.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit = {},
    onGoPreferences: () -> Unit = {},
    onGoSearchRecipes: () -> Unit = {},
    onGoCreateRecipe: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    LaunchedEffect(Unit) { if (ttsEnabled) speak("Bienvenido a la pantalla principal") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Inicio",
                centerIconRes = R.drawable.icon,
                onBack = null,
                onSettings = null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ===== HERO / BIENVENIDA =====
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "¡Bienvenido/a!",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        "Accede rápido a las funciones principales de NutriApp.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ===== ACCIONES PRINCIPALES (tarjetas grandes) =====
            ActionCard(
                title = "Buscar Recetas",
                subtitle = "Encuentra por nombre, categoría o calorías",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = Icons.Default.RestaurantMenu,
                iconDescription = "Buscar recetas",
                onClick = {
                    if (ttsEnabled) speak("Abrir buscar recetas")
                    onGoSearchRecipes()
                }
            )

            ActionCard(
                title = "Crear Receta",
                subtitle = "Añade una receta nueva a tu lista",
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                icon = Icons.Default.AddCircle,
                iconDescription = "Crear receta",
                onClick = {
                    if (ttsEnabled) speak("Abrir crear receta")
                    onGoCreateRecipe()
                }
            )

            // ===== PREFERENCIAS / ACCESIBILIDAD =====
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (ttsEnabled) speak("Abrir preferencias de accesibilidad")
                        onGoPreferences()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .heightIn(min = 72.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Accessibility,
                        contentDescription = "Preferencias de accesibilidad",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Preferencias de accesibilidad", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Tema, tamaño de texto, alto contraste y TTS",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    AssistChip(
                        onClick = {
                            if (ttsEnabled) speak("Abrir preferencias de accesibilidad")
                            onGoPreferences()
                        },
                        label = { Text("Abrir") }
                    )
                }
            }

            Spacer(Modifier.weight(1f)) // empuja el botón de cerrar al fondo

            // ===== CERRAR SESIÓN (acción destacada/destructiva) =====
            Button(
                onClick = {
                    if (ttsEnabled) speak("Cerrando sesión")
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // separación entre ícono y texto
                Text("Cerrar sesión")
            }
        }
    }
}

/** Tarjeta de acción grande y accesible */
@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    containerColor: Color,
    onContainerColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconDescription: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp) // área táctil generosa
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = onContainerColor
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = onContainerColor
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainerColor.copy(alpha = 0.9f)
                )
            }
        }
    }
}
