package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.firebase.FirebaseUserRepo
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotScreen(
    onBack: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    var emailOrUser by rememberSaveable { mutableStateOf("") }
    var lastMsg     by remember { mutableStateOf<String?>(null) }
    var loading     by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focus   = LocalFocusManager.current
    val scope   = rememberCoroutineScope()
    val canRecover = emailOrUser.isNotBlank()

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
                        "Ingresa tu email y te enviaremos un correo para restablecerla.",
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
                        value = emailOrUser,
                        onValueChange = { emailOrUser = it },
                        label = { Text("Email o usuario") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Usuario/Email") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { focus.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            loading = true
                            scope.launch {
                                try {
                                    FirebaseUserRepo.sendPasswordReset(emailOrUser.trim())
                                    val ok = "Correo de recuperación enviado"
                                    lastMsg = ok
                                    Toast.makeText(context, ok, Toast.LENGTH_SHORT).show()
                                    if (ttsEnabled) speak(ok)
                                    onBack()
                                } catch (e: Exception) {
                                    val msg = mapAuthError(e)
                                    lastMsg = msg
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (ttsEnabled) speak(msg)
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        enabled = canRecover && !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(if (loading) "Enviando…" else "Enviar correo")
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

/** Mapea errores comunes de Firebase Auth a mensajes entendibles */
private fun mapAuthError(e: Exception): String {
    val code = (e as? FirebaseAuthException)?.errorCode
    return when (code) {
        "ERROR_USER_NOT_FOUND"          -> "No existe un usuario con ese email."
        "ERROR_INVALID_EMAIL"           -> "Email con formato inválido."
        "ERROR_NETWORK_REQUEST_FAILED"  -> "Problema de red. Intenta nuevamente."
        else -> e.localizedMessage ?: "No fue posible enviar el correo."
    }
}
