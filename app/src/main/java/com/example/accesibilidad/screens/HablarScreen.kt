package com.example.accesibilidad.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.data.firebase.FirebaseUserRepo
import com.example.accesibilidad.data.firebase.FirestoreServices
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun HablarScreen() {
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val data = res.data
            val spokenText: ArrayList<String>? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = spokenText?.firstOrNull().orEmpty()
            resultText = text

            // ✅ Guardar en Firestore (solo texto transcrito)
            if (text.isNotBlank()) {
                val uid = FirebaseUserRepo.currentUserId
                if (uid == null) {
                    Toast.makeText(context, "Inicia sesión para guardar", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        try {
                            FirestoreServices.addSpeech(uid, text)
                            Toast.makeText(context, "Transcripción guardada", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al guardar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hablar", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                }
                launcher.launch(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Hablar ahora") }

        if (resultText.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Resultado: $resultText")
        }
    }
}
