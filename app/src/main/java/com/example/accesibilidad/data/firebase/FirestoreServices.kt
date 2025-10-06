package com.example.accesibilidad.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** Modelo con id para poder editar/eliminar desde la UI */
data class NoteDoc(
    val id: String,
    val text: String,
    val timestamp: Long
)

object FirestoreServices {

    private val db = FirebaseFirestore.getInstance()

    /** C: crear nota */
    suspend fun addNote(userId: String, text: String) {
        val noteData = hashMapOf(
            "userId" to userId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("notes").add(noteData).await()
    }

    /** R (simple): leer notas (sin id) */
    suspend fun getNotes(userId: String): List<Map<String, Any>> {
        val snapshot = db.collection("notes")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.data }
    }

    /** R (con id): leer notas incluyendo el id del documento (para Update/Delete) */
    suspend fun getNotesWithIds(userId: String): List<NoteDoc> {
        val snap = db.collection("notes")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snap.documents.mapNotNull { d ->
            val text = d.getString("text") ?: return@mapNotNull null
            val ts = d.getLong("timestamp") ?: 0L
            NoteDoc(id = d.id, text = text, timestamp = ts)
        }
    }

    /** U: actualizar texto de una nota */
    suspend fun updateNote(docId: String, newText: String) {
        db.collection("notes").document(docId)
            .update(
                mapOf(
                    "text" to newText.trim(),
                    "timestamp" to System.currentTimeMillis()
                )
            )
            .await()
    }

    /** D: eliminar una nota por id */
    suspend fun deleteNote(docId: String) {
        db.collection("notes").document(docId).delete().await()
    }

    /** Guarda transcripciones por voz (pantalla Hablar) */
    suspend fun addSpeech(userId: String, text: String) {
        val data = hashMapOf(
            "userId" to userId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("speech").add(data).await()
    }

    /** Guarda coordenadas de ubicación (pantalla BuscarDispositivo) */
    suspend fun addLocation(userId: String, lat: Double, lng: Double) {
        val locationData = hashMapOf(
            "userId" to userId,
            "latitude" to lat,
            "longitude" to lng,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("locations").add(locationData).await()
    }
}
