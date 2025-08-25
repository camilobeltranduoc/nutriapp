package com.example.accesibilidad.screens

// ===== IMPORTS =====
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.accesibilidad.R
import com.example.accesibilidad.data.UserRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onGoRegister: () -> Unit = {},
    onGoForgot: () -> Unit = {},
    onLoginOk: () -> Unit = {}
) {
    // ---- estados (saveable para no perderlos en rotación) ----
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPwd   by rememberSaveable { mutableStateOf(false) }
    var userError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    val focusManager      = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()

    val isLoginValid = username.isNotBlank() && password.isNotBlank()

    Scaffold(
        topBar = {
            AppTopBar(
                title = null,                         // sin texto a la izquierda
                centerIconRes = R.drawable.icon,      // logo centrado
                onBack = null,
                onSettings = null
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo arriba del formulario
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la app",
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // ===== Usuario =====
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    userError = if (it.isBlank()) "Usuario requerido" else null
                },
                label = { Text("Usuario") },
                singleLine = true,
                isError = userError != null,
                supportingText = { userError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),     // azul accesible
                    unfocusedBorderColor = Color(0xFF5F6368),   // gris neutro
                    cursorColor = Color(0xFF1565C0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF5F6368)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // ===== Contraseña (con mostrar/ocultar) =====
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
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = { showPwd = !showPwd }) {
                        androidx.compose.material3.Icon(
                            imageVector = if (showPwd) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPwd) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFF5F6368),
                    cursorColor = Color(0xFF1565C0),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF5F6368)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // ===== Botón Iniciar sesión =====
            Button(
                onClick = {
                    val ok = UserRepo.authenticate(username.trim(), password.trim())
                    if (ok) {
                        userError = null; passError = null
                        scope.launch { snackbarHostState.showSnackbar("Sesión iniciada") }
                        onLoginOk()
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Credenciales incorrectas") }
                    }
                },
                enabled = isLoginValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF9CCC9C),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Iniciar sesión") }

            // enlaces
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = onGoRegister) { Text("Crear cuenta", color = Color(0xFF1565C0)) }
                TextButton(onClick = onGoForgot)   { Text("Recuperar clave", color = Color(0xFF1565C0)) }
            }
        }
    }
}
