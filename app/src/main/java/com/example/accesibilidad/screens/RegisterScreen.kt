package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.firebase.FirebaseUserRepo
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    var user  by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pass  by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }

    var showPwd  by rememberSaveable { mutableStateOf(false) }
    var showPwd2 by rememberSaveable { mutableStateOf(false) }
    var loading  by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focus   = LocalFocusManager.current
    val scope   = rememberCoroutineScope()

    val isValid = user.isNotBlank() && email.isNotBlank() && pass.isNotBlank() && pass2.isNotBlank()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registro",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Crea tu cuenta",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text("Completa los datos para empezar a usar NutriApp.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Usuario") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                        supportingText = { Text("Debe incluir @ y dominio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") },
                        visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPwd = !showPwd }) {
                                Icon(if (showPwd) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pass2,
                        onValueChange = { pass2 = it },
                        label = { Text("Repite la contraseña") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Confirmar contraseña") },
                        visualTransformation = if (showPwd2) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPwd2 = !showPwd2 }) {
                                Icon(if (showPwd2) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val precheck = when {
                                user.isBlank() || pass.isBlank() || pass2.isBlank() || email.isBlank() ->
                                    "Completa todos los campos."
                                !email.contains("@") || !email.contains(".") ->
                                    "Email no válido."
                                pass != pass2 ->
                                    "Las contraseñas no coinciden."
                                pass.length < 6 ->
                                    "La contraseña debe tener al menos 6 caracteres."
                                else -> null
                            }
                            if (precheck != null) {
                                Toast.makeText(context, precheck, Toast.LENGTH_SHORT).show()
                                if (ttsEnabled) speak(precheck)
                                return@Button
                            }

                            // Registro REAL en Firebase con el EMAIL ingresado
                            loading = true
                            scope.launch {
                                try {
                                    FirebaseUserRepo.register(email.trim(), pass.trim())
                                    val done = "Usuario creado, inicia sesión."
                                    Toast.makeText(context, done, Toast.LENGTH_SHORT).show()
                                    if (ttsEnabled) speak(done)
                                    user = ""; email = ""; pass = ""; pass2 = ""
                                    onBack()
                                } catch (e: Exception) {
                                    val err = mapAuthError(e)
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    if (ttsEnabled) speak(err)
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        enabled = isValid && !loading,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        if (loading) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(12.dp))
                        }
                        Text(if (loading) "Registrando…" else "Registrar")
                    }
                }
            }
        }
    }
}

/** Mapea errores comunes de Firebase Auth a mensajes entendibles */
private fun mapAuthError(e: Exception): String {
    val code = (e as? FirebaseAuthException)?.errorCode
    return when (code) {
        "ERROR_EMAIL_ALREADY_IN_USE"  -> "Ese email ya está registrado."
        "ERROR_INVALID_EMAIL"         -> "Email con formato inválido."
        "ERROR_WEAK_PASSWORD"         -> "La contraseña debe tener al menos 6 caracteres."
        "ERROR_OPERATION_NOT_ALLOWED" -> "Método de registro deshabilitado en Firebase."
        "ERROR_NETWORK_REQUEST_FAILED"-> "Problema de red. Reintenta."
        else -> e.localizedMessage ?: "No fue posible registrar."
    }
}
