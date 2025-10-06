package com.example.accesibilidad.screens

// ===== IMPORTS =====
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.firebase.FirebaseUserRepo   // 👈 usa Firebase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onGoRegister: () -> Unit = {},
    onGoForgot: () -> Unit = {},
    onLoginOk: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPwd   by rememberSaveable { mutableStateOf(false) }
    var userError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var loading   by remember { mutableStateOf(false) }                // 👈 estado de carga

    val focusManager      = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()
    val context           = LocalContext.current                    // 👈 necesario para authenticate(context,...)

    val isLoginValid = username.isNotBlank() && password.isNotBlank()

    Scaffold(
        topBar = {
            AppTopBar(
                title = null,
                centerIconRes = R.drawable.icon,
                onBack = null,
                onSettings = null
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== HERO =====
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de la aplicación",
                        modifier = Modifier.size(96.dp)
                    )
                    Text("Bienvenido a NutriApp", style = MaterialTheme.typography.headlineSmall)
                    Text("Inicia sesión para gestionar tus recetas y preferencias.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ===== FORM =====
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            userError = if (it.isBlank()) "Usuario requerido" else null
                        },
                        label = { Text("Usuario o email") }, // 👈 puede ser "ana" o "ana@mail.com"
                        singleLine = true,
                        isError = userError != null,
                        supportingText = { userError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Usuario") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passError = if (it.isBlank()) "Contraseña requerida" else null
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = passError != null,
                        supportingText = { passError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") },
                        trailingIcon = {
                            IconButton(onClick = { showPwd = !showPwd }) {
                                Icon(
                                    imageVector = if (showPwd) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showPwd) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ===== BOTÓN LOGIN (Firebase) =====
                    Button(
                        onClick = {
                            loading = true
                            scope.launch {
                                val ok = try {
                                    // ✅ Autentica en Firebase y cachea uid/email/displayName en SharedPreferences
                                    FirebaseUserRepo.authenticate(
                                        context = context,
                                        username = username.trim(),
                                        password = password.trim()
                                    )
                                } catch (e: Exception) {
                                    false
                                }
                                loading = false
                                if (ok) {
                                    val uid = FirebaseUserRepo.currentUserId ?: "—"
                                    snackbarHostState.showSnackbar("Sesión iniciada (uid: $uid)")
                                    if (ttsEnabled) speak("Sesión iniciada correctamente")
                                    onLoginOk()
                                } else {
                                    snackbarHostState.showSnackbar("Credenciales incorrectas")
                                    if (ttsEnabled) speak("Usuario o contraseña inválidos")
                                }
                            }
                        },
                        enabled = isLoginValid && !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(12.dp))
                        }
                        Text(if (loading) "Ingresando…" else "Iniciar sesión")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                    ) {
                        AssistChip(onClick = onGoRegister, label = { Text("Crear cuenta") })
                        AssistChip(onClick = onGoForgot,   label = { Text("Recuperar clave") })
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Text(
                "Usa Preferencias para activar lectura por voz y alto contraste.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
