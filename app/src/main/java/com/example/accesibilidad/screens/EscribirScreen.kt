package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.accessibility.TtsController
import com.example.accesibilidad.data.firebase.FirebaseUserRepo
import com.example.accesibilidad.data.firebase.FirestoreServices
import com.example.accesibilidad.data.firebase.NoteDoc
import kotlinx.coroutines.launch

@Composable
fun EscribirScreen() {
    val context = LocalContext.current
    val tts = remember { TtsController(context) }
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf(TextFieldValue("")) }

    // 🔄 estado para lista de notas del usuario
    var notes by remember { mutableStateOf<List<NoteDoc>>(emptyList()) }
    var loadingList by remember { mutableStateOf(false) }

    // ✏️ estado de edición
    var editing by remember { mutableStateOf<NoteDoc?>(null) }
    var editText by remember { mutableStateOf(TextFieldValue("")) }
    var savingEdit by remember { mutableStateOf(false) }

    // cargar notas al entrar
    LaunchedEffect(Unit) {
        val uid = FirebaseUserRepo.currentUserId
        if (uid != null) {
            loadingList = true
            try {
                notes = FirestoreServices.getNotesWithIds(uid)
            } catch (_: Exception) { /* opcional: mostrar error */ }
            loadingList = false
        }
    }

    fun refreshNotes() {
        val uid = FirebaseUserRepo.currentUserId ?: return
        scope.launch {
            loadingList = true
            try { notes = FirestoreServices.getNotesWithIds(uid) } catch (_: Exception) {}
            loadingList = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Escribir mensaje", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Mensaje") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val mensaje = text.text.trim()
                if (mensaje.isBlank()) {
                    Toast.makeText(context, "Campo vacío", Toast.LENGTH_SHORT).show()
                    tts.speak("Por favor escribe algo")
                    return@Button
                }
                val uid = FirebaseUserRepo.currentUserId
                if (uid == null) {
                    Toast.makeText(context, "Inicia sesión para guardar", Toast.LENGTH_SHORT).show()
                    tts.speak("Debes iniciar sesión para guardar el mensaje")
                    return@Button
                }
                scope.launch {
                    try {
                        FirestoreServices.addNote(uid, mensaje)
                        Toast.makeText(context, "Mensaje guardado en la nube", Toast.LENGTH_SHORT).show()
                        tts.speak("Mensaje guardado correctamente")
                        text = TextFieldValue("")
                        refreshNotes() // 🔄 recargar lista
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al guardar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        tts.speak("Ocurrió un error al guardar el mensaje")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }

        Divider()

        Text("Mis mensajes", style = MaterialTheme.typography.titleMedium)

        if (loadingList) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notes, key = { it.id }) { note ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(note.text, modifier = Modifier.weight(1f))
                            Row {
                                IconButton(onClick = {
                                    editing = note
                                    editText = TextFieldValue(note.text)
                                }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }

                                IconButton(onClick = {
                                    val uid = FirebaseUserRepo.currentUserId
                                    if (uid == null) {
                                        Toast.makeText(context, "Inicia sesión", Toast.LENGTH_SHORT).show()
                                    } else {
                                        scope.launch {
                                            try {
                                                FirestoreServices.deleteNote(note.id)
                                                Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show()
                                                tts.speak("Mensaje eliminado")
                                                refreshNotes()
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
                            }
                        }
                    }
                }
            }
        }
    }

    // 🪟 Diálogo de edición
    if (editing != null) {
        AlertDialog(
            onDismissRequest = { if (!savingEdit) editing = null },
            title = { Text("Editar mensaje") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val newText = editText.text.trim()
                    if (newText.isBlank()) return@TextButton
                    savingEdit = true
                    scope.launch {
                        try {
                            FirestoreServices.updateNote(editing!!.id, newText)
                            Toast.makeText(context, "Actualizado", Toast.LENGTH_SHORT).show()
                            tts.speak("Mensaje actualizado")
                            editing = null
                            refreshNotes()
                        } catch (_: Exception) {
                            Toast.makeText(context, "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                        } finally { savingEdit = false }
                    }
                }, enabled = !savingEdit) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { if (!savingEdit) editing = null }) { Text("Cancelar") }
            }
        )
    }
}
