package com.example.accesibilidad.screens

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.firebase.FirebaseUserRepo
import com.example.accesibilidad.data.firebase.FirestoreServices
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun BuscarDispositivoScreen(
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var coords by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf<Boolean?>(null) }
    val scope = rememberCoroutineScope()

    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun saveLocationIfPossible(lat: Double, lng: Double) {
        val uid = FirebaseUserRepo.currentUserId
        if (uid == null) {
            Toast.makeText(context, "Inicia sesión para guardar ubicación", Toast.LENGTH_SHORT).show()
            if (ttsEnabled) speak("Debes iniciar sesión para guardar la ubicación")
            return
        }
        scope.launch {
            try {
                FirestoreServices.addLocation(uid, lat, lng)
                Toast.makeText(context, "Ubicación guardada en la nube", Toast.LENGTH_SHORT).show()
                if (ttsEnabled) speak("Ubicación guardada en la nube")
            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                if (ttsEnabled) speak("Ocurrió un error al guardar la ubicación")
            }
        }
    }

    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (!granted) {
            coords = null
            if (ttsEnabled) speak("Permiso de ubicación denegado")
        } else {
            if (ttsEnabled) speak("Permiso de ubicación concedido")
            loading = true
            fused.lastLocation.addOnSuccessListener { loc ->
                loading = false
                coords = if (loc != null) "Lat: ${loc.latitude}, Lng: ${loc.longitude}" else null
                if (loc != null) {
                    if (ttsEnabled) speak("Ubicación obtenida")
                    // ✅ Guardar en Firestore
                    saveLocationIfPossible(loc.latitude, loc.longitude)
                } else {
                    if (ttsEnabled) speak("No fue posible obtener la ubicación")
                }
            }.addOnFailureListener {
                loading = false
                coords = null
                if (ttsEnabled) speak("Ocurrió un error al obtener la ubicación")
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Buscar dispositivo",
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
            // Header card
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Ubicación",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Ubicación actual",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Obtén las coordenadas del dispositivo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Estado de permiso
            PermissionPill(permissionGranted = permissionGranted)

            // Tarjeta de coordenadas
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Coordenadas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (loading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(strokeWidth = 3.dp)
                            Text("Obteniendo ubicación…")
                        }
                    } else if (coords != null) {
                        Text(
                            coords!!,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(coords!!))
                                    if (ttsEnabled) speak("Coordenadas copiadas")
                                }
                            ) { Text("Copiar") }

                            OutlinedButton(onClick = {
                                // refrescar y volver a guardar
                                permissionGranted?.let { granted ->
                                    if (granted) {
                                        loading = true
                                        fused.lastLocation.addOnSuccessListener { loc ->
                                            loading = false
                                            if (loc != null) {
                                                coords = "Lat: ${loc.latitude}, Lng: ${loc.longitude}"
                                                saveLocationIfPossible(loc.latitude, loc.longitude) // ✅ guarda de nuevo
                                            }
                                        }.addOnFailureListener { loading = false }
                                    } else {
                                        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                } ?: requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }) { Text("Actualizar") }
                        }
                    } else {
                        Text(
                            "Aún no hay coordenadas",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // CTA principal
            Button(
                onClick = { requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Text(if (permissionGranted == true) "Obtener ubicación" else "Permitir ubicación")
            }
        }
    }
}

@Composable
private fun PermissionPill(permissionGranted: Boolean?) {
    val (bg, fg, text) = when (permissionGranted) {
        null -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "Permiso: sin solicitar"
        )
        true -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Permiso: concedido"
        )
        else -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Permiso: denegado"
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text, color = fg, style = MaterialTheme.typography.bodyMedium)
    }
}
