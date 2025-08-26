package com.example.accesibilidad.screens

// ===== IMPORTS =====
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import com.example.accesibilidad.R
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    onBack: () -> Unit = {}
) {
    // Estados “demo” (se podrían persistir luego con DataStore/Room)
    var fontChoice by remember { mutableStateOf("Mediano") }
    var tts by remember { mutableStateOf(false) }
    var highContrast by remember { mutableStateOf(true) }
    val themes = listOf("Sistema", "Claro", "Oscuro")
    var theme by remember { mutableStateOf(themes.first()) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Preferencias",
                centerIconRes = R.drawable.icon,
                onBack = onBack,
                onSettings = null
            )
        },
        // Botón fijo abajo
        bottomBar = {
            val context = LocalContext.current
            Box(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        // Mostrar feedback
                        Toast.makeText(context, "Preferencias guardadas ✅", Toast.LENGTH_SHORT).show()
                        // Volver a la pantalla anterior
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar cambios")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()) // <— contenido desplazable
                .imePadding()                           // <— sube con el teclado
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Preferencias de accesibilidad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // ===== RADIO BUTTONS: tamaño de texto =====
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tamaño de texto")
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    listOf("Pequeño", "Mediano", "Grande").forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = fontChoice == option,
                                onClick = { fontChoice = option }
                            )
                            Text(option, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }

            // ===== CHECK LIST =====
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Opciones")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = tts, onCheckedChange = { tts = it })
                    Text("Leer en voz alta (TTS)")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = highContrast, onCheckedChange = { highContrast = it })
                    Text("Alto contraste")
                }
            }

            // ===== COMBO BOX (ExposedDropdownMenu) =====
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    value = theme,
                    onValueChange = {},
                    label = { Text("Tema") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    themes.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = { theme = item; expanded = false }
                        )
                    }
                }
            }

            // ===== “TABLA” (key/value) =====
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Resumen", fontWeight = FontWeight.SemiBold)
                PrefRow("Tamaño de texto", fontChoice)
                PrefRow("TTS", if (tts) "Activado" else "Desactivado")
                PrefRow("Contraste", if (highContrast) "Alto" else "Normal")
                PrefRow("Tema", theme)
            }

            // ===== GRILLA (ejemplo) =====
            Text("Accesos rápidos", fontWeight = FontWeight.SemiBold)
            val actions = listOf("Recetas", "Mis Recetas", "Crear Recetas")
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(actions) { a ->
                    ElevatedCard(
                        onClick = { /* TODO */ },
                        modifier = Modifier.height(80.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(a)
                        }
                    }
                }
            }

            // NO pongas más botón aquí: ya existe en bottomBar 👆
            // Deja, si quieres, un pequeño espacio final para estética:
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PrefRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value)
    }
}