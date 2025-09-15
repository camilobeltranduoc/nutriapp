package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.UserRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotScreen(
    onBack: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    var user by remember { mutableStateOf("") }
    var lastMsg by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val focus = LocalFocusManager.current
    val canRecover = user.isNotBlank()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Recuperar clave",
                centerIconRes = R.drawable.icon,
                onBack = onBack,
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

            // Hero
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        "Ingresa tu usuario para mostrar la contraseña registrada (solo con fines de demo).",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Formulario
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.AccountCircle, contentDescription = "Usuario")
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focus.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val pwd = UserRepo.getPassword(user.trim())
                            val msg = if (pwd != null) {
                                "Tu contraseña registrada es: $pwd"
                            } else {
                                "Usuario no encontrado."
                            }
                            lastMsg = msg
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (ttsEnabled) speak(msg)
                        },
                        enabled = canRecover,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                    ) {
                        Text("Recuperar")
                    }
                }
            }

            // Resultado
            lastMsg?.let { m ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Resultado") },
                        supportingContent = { Text(m) }
                    )
                }
            }
        }
    }
}
