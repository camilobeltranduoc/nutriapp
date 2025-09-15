package com.example.accesibilidad.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.ui.theme.AppThemeMode
import com.example.accesibilidad.ui.theme.TextSizePref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    highContrast: Boolean,
    onHighContrastChange: (Boolean) -> Unit,
    textSizePref: TextSizePref,
    onTextSizeChange: (TextSizePref) -> Unit,
    ttsEnabled: Boolean,
    onTtsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    speak: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    val themeLabel = when (themeMode) {
        AppThemeMode.System -> "Sistema"
        AppThemeMode.Light  -> "Claro"
        AppThemeMode.Dark   -> "Oscuro"
    }
    val textSizeLabel = when (textSizePref) {
        TextSizePref.Small  -> "Pequeño"
        TextSizePref.Medium -> "Mediano"
        TextSizePref.Large  -> "Grande"
    }

    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Preferencias de Accesibilidad",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ───────── Tamaño de texto ─────────
            SectionCard(
                icon = Icons.Default.TextFields,
                title = "Tamaño de texto"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RadioItem(
                            selected = textSizePref == TextSizePref.Small,
                            label = "Pequeño",
                            onClick = {
                                onTextSizeChange(TextSizePref.Small)
                                if (ttsEnabled) speak("Tamaño de texto pequeño")
                            }
                        )
                        RadioItem(
                            selected = textSizePref == TextSizePref.Medium,
                            label = "Mediano",
                            onClick = {
                                onTextSizeChange(TextSizePref.Medium)
                                if (ttsEnabled) speak("Tamaño de texto mediano")
                            }
                        )
                        RadioItem(
                            selected = textSizePref == TextSizePref.Large,
                            label = "Grande",
                            onClick = {
                                onTextSizeChange(TextSizePref.Large)
                                if (ttsEnabled) speak("Tamaño de texto grande")
                            }
                        )
                    }

                    // Ejemplo de vista previa con el tamaño elegido
                    Text(
                        "Vista previa de texto",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // ───────── Accesibilidad ─────────
            SectionCard(
                icon = Icons.Default.Accessibility,
                title = "Accesibilidad"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckboxItem(
                        checked = ttsEnabled,
                        label = "Activar lectura por voz (TTS)",
                        onCheckedChange = { checked ->
                            onTtsChange(checked)
                            // Evitamos hablar cuando el usuario apaga TTS
                            if (checked) speak("Text to Speech activado")
                        }
                    )
                    CheckboxItem(
                        checked = highContrast,
                        label = "Alto contraste",
                        onCheckedChange = { checked ->
                            onHighContrastChange(checked)
                            if (ttsEnabled) speak(if (checked) "Alto contraste activado" else "Alto contraste desactivado")
                        }
                    )
                }
            }

            // ───────── Tema ─────────
            SectionCard(
                icon = Icons.Default.ColorLens,
                title = "Tema de la aplicación"
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = themeLabel,
                        onValueChange = {},
                        label = { Text("Tema") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sistema") },
                            onClick = {
                                onThemeModeChange(AppThemeMode.System)
                                expanded = false
                                if (ttsEnabled) speak("Tema sistema")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Claro") },
                            onClick = {
                                onThemeModeChange(AppThemeMode.Light)
                                expanded = false
                                if (ttsEnabled) speak("Tema claro")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Oscuro") },
                            onClick = {
                                onThemeModeChange(AppThemeMode.Dark)
                                expanded = false
                                if (ttsEnabled) speak("Tema oscuro")
                            }
                        )
                    }
                }
            }

            // ───────── Resumen ─────────
            SectionCard(icon = Icons.Default.Tune, title = "Resumen de selección") {
                SummaryTable(
                    textSizeLabel = textSizeLabel,
                    ttsOn = ttsEnabled,
                    highContrastOn = highContrast,
                    themeLabel = themeLabel
                )
            }

            // ───────── Link informativo ─────────
            ElevatedCard {
                ListItem(
                    headlineContent = { Text("Más sobre accesibilidad en Android") },
                    supportingContent = { Text("developer.android.com • Guía oficial") },
                    trailingContent = {
                        AssistChip(onClick = {
                            uriHandler.openUri(
                                "https://developer.android.com/guide/topics/ui/accessibility?hl=es-419"
                            )
                        }, label = { Text("Abrir") })
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            // ───────── Guardar ─────────
            Button(
                onClick = {
                    if (ttsEnabled) speak("Preferencias guardadas")
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar cambios") }
        }
    }
}

/* ------------------------- Auxiliares de UI ------------------------- */

@Composable
private fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
            }
            content()
        }
    }
}

@Composable
private fun RadioItem(selected: Boolean, label: String, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.heightIn(min = 48.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}

@Composable
private fun CheckboxItem(checked: Boolean, label: String, onCheckedChange: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.heightIn(min = 48.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}

@Composable
private fun SummaryTable(
    textSizeLabel: String,
    ttsOn: Boolean,
    highContrastOn: Boolean,
    themeLabel: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        RowTitle("Ajuste", "Valor")
        RowItem("Tamaño de texto", textSizeLabel)
        RowItem("TTS", if (ttsOn) "Activado" else "Desactivado")
        RowItem("Alto contraste", if (highContrastOn) "Activado" else "Desactivado")
        RowItem("Tema", themeLabel)
    }
}

@Composable
private fun RowTitle(left: String, right: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(left, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
        Text(right, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun RowItem(left: String, right: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(left, modifier = Modifier.weight(1f))
        Text(right, modifier = Modifier.weight(1f))
    }
}
