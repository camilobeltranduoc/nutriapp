package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.accesibilidad.ui.theme.AppThemeMode
import com.example.accesibilidad.ui.theme.TextSizePref
import kotlinx.coroutines.launch

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
    val themeLabel = when (themeMode) {
        AppThemeMode.System -> "Sistema"
        AppThemeMode.Light -> "Claro"
        AppThemeMode.Dark -> "Oscuro"
    }
    val textSizeLabel = when (textSizePref) {
        TextSizePref.Small -> "Pequeño"
        TextSizePref.Medium -> "Mediano"
        TextSizePref.Large -> "Grande"
    }
    val uriHandler = LocalUriHandler.current
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Preferencias de Accesibilidad", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ───── Cuenta: info + modificar + logout
            item {
                AccountInfoCard(
                    ttsEnabled = ttsEnabled,
                    speak = speak,
                    onBack = onBack
                )
            }

            // ───── Tamaño de texto
            item {
                SectionCard(Icons.Default.TextFields, "Tamaño de texto") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                "Pequeño" to TextSizePref.Small,
                                "Mediano" to TextSizePref.Medium,
                                "Grande" to TextSizePref.Large
                            ).forEach { (label, size) ->
                                RadioItem(
                                    selected = textSizePref == size,
                                    label = label,
                                    onClick = {
                                        onTextSizeChange(size)
                                        if (ttsEnabled) speak("Tamaño de texto $label")
                                    }
                                )
                            }
                        }
                        Text("Vista previa de texto", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // ───── Accesibilidad
            item {
                SectionCard(Icons.Default.Accessibility, "Accesibilidad") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CheckboxItem(
                            checked = ttsEnabled,
                            label = "Activar lectura por voz (TTS)",
                            onCheckedChange = { checked ->
                                onTtsChange(checked)
                                if (checked) speak("Text to Speech activado")
                            }
                        )
                        CheckboxItem(
                            checked = highContrast,
                            label = "Alto contraste",
                            onCheckedChange = { checked ->
                                onHighContrastChange(checked)
                                if (ttsEnabled)
                                    speak(if (checked) "Alto contraste activado" else "Alto contraste desactivado")
                            }
                        )
                    }
                }
            }

            // ───── Tema
            item {
                SectionCard(Icons.Default.ColorLens, "Tema de la aplicación") {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = themeLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tema") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf(
                                "Sistema" to AppThemeMode.System,
                                "Claro" to AppThemeMode.Light,
                                "Oscuro" to AppThemeMode.Dark
                            ).forEach { (label, mode) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        onThemeModeChange(mode)
                                        expanded = false
                                        if (ttsEnabled) speak("Tema $label")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ───── Resumen
            item {
                SectionCard(Icons.Default.Tune, "Resumen de selección") {
                    SummaryTable(textSizeLabel, ttsEnabled, highContrast, themeLabel)
                }
            }

            // ───── Seguridad (password / delete)
            item {
                AccountSettingsCard(ttsEnabled = ttsEnabled, speak = speak, onBack = onBack)
            }

            // ───── Link informativo
            item {
                ElevatedCard {
                    ListItem(
                        headlineContent = { Text("Más sobre accesibilidad en Android") },
                        supportingContent = { Text("developer.android.com • Guía oficial") },
                        trailingContent = {
                            AssistChip(
                                onClick = {
                                    uriHandler.openUri(
                                        "https://developer.android.com/guide/topics/ui/accessibility?hl=es-419"
                                    )
                                },
                                label = { Text("Abrir") }
                            )
                        }
                    )
                }
            }

            // ───── Botón guardar (al final, pero dentro de la lista para que sea alcanzable con scroll)
            item {
                Button(
                    onClick = {
                        if (ttsEnabled) speak("Preferencias guardadas")
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar cambios") }
            }

            // Un pequeño espacio final
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/* ---------- Auxiliares ---------- */
@Composable
private fun SectionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
            }
            content()
        }
    }
}

@Composable private fun RadioItem(selected: Boolean, label: String, onClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected, onClick)
        Text(label)
    }
}

@Composable private fun CheckboxItem(checked: Boolean, label: String, onCheckedChange: (Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, onCheckedChange)
        Text(label)
    }
}

@Composable private fun SummaryTable(textSizeLabel: String, ttsOn: Boolean, highContrastOn: Boolean, themeLabel: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        RowTitle("Ajuste", "Valor")
        RowItem("Tamaño de texto", textSizeLabel)
        RowItem("TTS", if (ttsOn) "Activado" else "Desactivado")
        RowItem("Alto contraste", if (highContrastOn) "Activado" else "Desactivado")
        RowItem("Tema", themeLabel)
    }
}

@Composable private fun RowTitle(l: String, r: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(l, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
        Text(r, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
    }
}

@Composable private fun RowItem(l: String, r: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(l, modifier = Modifier.weight(1f))
        Text(r, modifier = Modifier.weight(1f))
    }
}

/* ---------- Cuenta (leer, editar, logout) ---------- */
@Composable
private fun AccountInfoCard(ttsEnabled: Boolean, speak: (String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf<String?>(null) }
    var uid by remember { mutableStateOf<String?>(null) }
    var displayName by remember { mutableStateOf<String?>(null) }

    var showEdit by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var draftName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val cached = com.example.accesibilidad.data.firebase.FirebaseUserRepo.readCachedUser(context)
        email = cached?.email
        uid = cached?.uid
        displayName = cached?.displayName
    }

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                Text("Cuenta", style = MaterialTheme.typography.titleMedium)
            }

            RowItem("Nombre", displayName ?: "—")
            RowItem("Email", email ?: "—")
            RowItem("UID", uid ?: "—")

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    draftName = displayName ?: ""
                    showEdit = true
                }) { Text("Modificar") }

                OutlinedButton(
                    onClick = {
                        com.example.accesibilidad.data.firebase.FirebaseUserRepo.logoutAndClear(context)
                        if (ttsEnabled) speak("Sesión cerrada")
                        Toast.makeText(context, "Sesión cerrada.", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                ) { Text("Cerrar sesión") }
            }
        }
    }

    if (showEdit) {
        AlertDialog(
            onDismissRequest = { if (!saving) showEdit = false },
            title = { Text("Editar nombre") },
            text = {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    label = { Text("Nombre para mostrar") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (draftName.isBlank()) {
                            Toast.makeText(context, "Ingresa un nombre.", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        saving = true
                        scope.launch {
                            val ok = com.example.accesibilidad.data.firebase.FirebaseUserRepo
                                .updateDisplayName(context, draftName)
                            saving = false
                            if (ok) {
                                displayName = draftName
                                if (ttsEnabled) speak("Nombre actualizado")
                                Toast.makeText(context, "Nombre actualizado.", Toast.LENGTH_SHORT).show()
                                showEdit = false
                            } else {
                                Toast.makeText(context, "No se pudo actualizar.", Toast.LENGTH_SHORT).show()
                                if (ttsEnabled) speak("No se pudo actualizar el nombre")
                            }
                        }
                    },
                    enabled = !saving
                ) { Text(if (saving) "Guardando…" else "Guardar") }
            },
            dismissButton = { TextButton(onClick = { if (!saving) showEdit = false }) { Text("Cancelar") } }
        )
    }
}

/* ---------- Seguridad (contraseña / eliminar cuenta) ---------- */
@Composable
private fun AccountSettingsCard(ttsEnabled: Boolean, speak: (String) -> Unit, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var loadingUpdate by remember { mutableStateOf(false) }
    var loadingDelete by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Seguridad de la cuenta", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = currentPass, onValueChange = { currentPass = it },
                label = { Text("Contraseña actual") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newPass, onValueChange = { newPass = it },
                label = { Text("Nueva contraseña (min 6)") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (currentPass.isBlank() || newPass.length < 6) {
                        Toast.makeText(context, "Completa los campos.", Toast.LENGTH_SHORT).show()
                        if (ttsEnabled) speak("Completa los campos para cambiar la contraseña")
                        return@Button
                    }
                    loadingUpdate = true
                    scope.launch {
                        val ok = com.example.accesibilidad.data.firebase.FirebaseUserRepo.updatePassword(
                            currentPassword = currentPass.trim(),
                            newPassword = newPass.trim()
                        )
                        loadingUpdate = false
                        if (ok) {
                            Toast.makeText(context, "Contraseña actualizada.", Toast.LENGTH_SHORT).show()
                            if (ttsEnabled) speak("Contraseña actualizada")
                            currentPass = ""; newPass = ""
                        } else {
                            Toast.makeText(context, "Error al actualizar.", Toast.LENGTH_SHORT).show()
                            if (ttsEnabled) speak("No se pudo actualizar la contraseña")
                        }
                    }
                },
                enabled = !loadingUpdate,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loadingUpdate) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text(if (loadingUpdate) "Actualizando…" else "Cambiar contraseña")
            }

            Divider()

            OutlinedTextField(
                value = currentPass,
                onValueChange = { currentPass = it },
                label = { Text("Confirma con tu contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(
                onClick = { showConfirmDelete = true },
                enabled = currentPass.isNotBlank() && !loadingDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Eliminar cuenta") }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { if (!loadingDelete) showConfirmDelete = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("Esta acción es permanente. ¿Deseas eliminar tu cuenta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        loadingDelete = true
                        scope.launch {
                            val ok = com.example.accesibilidad.data.firebase.FirebaseUserRepo.deleteAccount(currentPass.trim())
                            loadingDelete = false
                            if (ok) {
                                com.example.accesibilidad.data.firebase.FirebaseUserRepo.logoutAndClear(context)
                                if (ttsEnabled) speak("Cuenta eliminada")
                                Toast.makeText(context, "Cuenta eliminada.", Toast.LENGTH_SHORT).show()
                                showConfirmDelete = false
                                onBack()
                            } else {
                                Toast.makeText(context, "No se pudo eliminar.", Toast.LENGTH_SHORT).show()
                                if (ttsEnabled) speak("No se pudo eliminar la cuenta")
                            }
                        }
                    },
                    enabled = !loadingDelete
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { if (!loadingDelete) showConfirmDelete = false }) { Text("Cancelar") } }
        )
    }
}
