package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.firebase.FirebaseUserRepo
import com.example.accesibilidad.data.firebase.FirestoreServices
import com.example.accesibilidad.data.firebase.NoteDoc
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit = {},
    onGoPreferences: () -> Unit = {},
    onGoSearchRecipes: () -> Unit = {},
    onGoCreateRecipe: () -> Unit = {},
    onGoBuscarDispositivo: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    LaunchedEffect(Unit) { if (ttsEnabled) speak("Bienvenido a la pantalla principal") }

    // Hoja para CRUD de notas
    var showNotesSheet by remember { mutableStateOf(false) }

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
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
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

            // ===== ACCIONES PRINCIPALES =====
            ActionCard(
                title = "Buscar Recetas",
                subtitle = "Encuentra por nombre, categoría o calorías",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = Icons.Default.RestaurantMenu,
                iconDescription = "Buscar recetas"
            ) {
                if (ttsEnabled) speak("Abrir buscar recetas")
                onGoSearchRecipes()
            }

            ActionCard(
                title = "Crear Receta",
                subtitle = "Añade una receta nueva a tu lista",
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                icon = Icons.Default.AddCircle,
                iconDescription = "Crear receta"
            ) {
                if (ttsEnabled) speak("Abrir crear receta")
                onGoCreateRecipe()
            }

            // ===== CRUD de notas en MISMA pantalla =====
            ActionCard(
                title = "Mis notas (CRUD)",
                subtitle = "Ver, editar y eliminar",
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
                icon = Icons.Default.Edit,
                iconDescription = "Notas"
            ) {
                if (ttsEnabled) speak("Abrir mis notas")
                showNotesSheet = true
            }

            // ===== BUSCAR DISPOSITIVO =====
            ActionCard(
                title = "Buscar dispositivo",
                subtitle = "Obtener ubicación actual",
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                onContainerColor = MaterialTheme.colorScheme.onPrimary,
                icon = Icons.Default.MyLocation,
                iconDescription = "Ubicación actual"
            ) {
                if (ttsEnabled) speak("Abrir buscar dispositivo")
                onGoBuscarDispositivo()
            }

            // ===== PREFERENCIAS =====
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

            Spacer(Modifier.weight(1f))

            // ===== CERRAR SESIÓN =====
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
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }

        // Hoja modal CRUD
        if (showNotesSheet) {
            NotesCrudBottomSheet(
                onDismiss = { showNotesSheet = false },
                ttsEnabled = ttsEnabled,
                speak = speak
            )
        }
    }
}

/** Tarjeta de acción reutilizable */
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
            .heightIn(min = 96.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = iconDescription, tint = onContainerColor)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = onContainerColor)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = onContainerColor.copy(alpha = 0.9f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesCrudBottomSheet(
    onDismiss: () -> Unit,
    ttsEnabled: Boolean,
    speak: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var loading by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf(emptyList<NoteDoc>()) }

    // === Crear nota (Create)
    var newText by remember { mutableStateOf("") }
    var creating by remember { mutableStateOf(false) }

    // === Editar nota (Update)
    var editing by remember { mutableStateOf<NoteDoc?>(null) }
    var editText by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }

    fun refresh() {
        val uid = FirebaseUserRepo.currentUserId ?: return
        scope.launch {
            loading = true
            try {
                notes = FirestoreServices.getNotesWithIds(uid)
            } catch (_: Exception) {
                Toast.makeText(context, "No se pudieron cargar notas", Toast.LENGTH_SHORT).show()
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { refresh() }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Mis notas", style = MaterialTheme.typography.titleLarge)

            // ======= CREAR NOTA (CREATE) =======
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nueva nota", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = newText,
                        onValueChange = { newText = it.take(300) },
                        placeholder = { Text("Escribe tu nota…") },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val txt = newText.trim()
                                if (txt.isBlank()) {
                                    Toast.makeText(context, "La nota está vacía", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val uid = FirebaseUserRepo.currentUserId
                                if (uid == null) {
                                    Toast.makeText(context, "Inicia sesión para guardar", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                creating = true
                                scope.launch {
                                    try {
                                        FirestoreServices.addNote(uid, txt)
                                        if (ttsEnabled) speak("Nota creada")
                                        Toast.makeText(context, "Nota creada", Toast.LENGTH_SHORT).show()
                                        newText = ""
                                        refresh()
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "Error al crear nota", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        creating = false
                                    }
                                }
                            },
                            enabled = !creating && newText.isNotBlank()
                        ) {
                            if (creating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(if (creating) "Guardando…" else "Agregar")
                        }

                        TextButton(onClick = { newText = "" }, enabled = newText.isNotBlank()) {
                            Text("Limpiar")
                        }
                    }
                }
            }

            // ======= LISTA (READ) + ACCIONES (UPDATE/DELETE) =======
            if (loading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Cargando…")
                }
            } else if (notes.isEmpty()) {
                Text("No hay notas aún. Agrega una con el formulario de arriba.")
            } else {
                notes.forEach { note ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(note.text, modifier = Modifier.weight(1f))
                            Row {
                                IconButton(onClick = {
                                    editing = note
                                    editText = note.text
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        try {
                                            FirestoreServices.deleteNote(docId = note.id)
                                            if (ttsEnabled) speak("Nota eliminada")
                                            Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show()
                                            refresh()
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { refresh() }) { Text("Actualizar lista") }
            Spacer(Modifier.height(8.dp))
        }
    }

    // ======= DIÁLOGO EDITAR (UPDATE) =======
    if (editing != null) {
        AlertDialog(
            onDismissRequest = { if (!saving) editing = null },
            title = { Text("Editar nota") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it.take(300) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newTxt = editText.trim()
                        if (newTxt.isBlank()) return@TextButton
                        saving = true
                        scope.launch {
                            try {
                                FirestoreServices.updateNote(editing!!.id, newTxt)
                                if (ttsEnabled) speak("Nota actualizada")
                                Toast.makeText(context, "Actualizado", Toast.LENGTH_SHORT).show()
                                editing = null
                                refresh()
                            } catch (_: Exception) {
                                Toast.makeText(context, "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                            } finally {
                                saving = false
                            }
                        }
                    },
                    enabled = !saving
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { if (!saving) editing = null }) { Text("Cancelar") }
            }
        )
    }
}
