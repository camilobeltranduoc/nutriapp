package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.User
import com.example.accesibilidad.data.UserRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit = {}) {
    // Estados (saveable para rotación)
    var user  by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pass  by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }

    // Mostrar/ocultar contraseña
    var showPwd  by rememberSaveable { mutableStateOf(false) }
    var showPwd2 by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val focus   = LocalFocusManager.current

    // Validez mínima del formulario
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Usuario
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuario") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFF5F6368),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF5F6368),
                    cursorColor = Color(0xFF1565C0)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focus.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFF5F6368),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF5F6368),
                    cursorColor = Color(0xFF1565C0)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focus.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPwd = !showPwd }) {
                        Icon(
                            imageVector = if (showPwd) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPwd) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFF5F6368),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF5F6368),
                    cursorColor = Color(0xFF1565C0)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focus.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Repite la contraseña
            OutlinedTextField(
                value = pass2,
                onValueChange = { pass2 = it },
                label = { Text("Repite la contraseña") },
                singleLine = true,
                visualTransformation = if (showPwd2) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPwd2 = !showPwd2 }) {
                        Icon(
                            imageVector = if (showPwd2) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPwd2) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFF5F6368),
                    focusedLabelColor = Color(0xFF1565C0),
                    unfocusedLabelColor = Color(0xFF5F6368),
                    cursorColor = Color(0xFF1565C0)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focus.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Botón registrar
            Button(
                onClick = {
                    when {
                        user.isBlank() || pass.isBlank() || pass2.isBlank() || email.isBlank() -> {
                            Toast.makeText(context, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
                        }
                        !email.contains("@") || !email.contains(".") -> {
                            Toast.makeText(context, "Email no válido.", Toast.LENGTH_SHORT).show()
                        }
                        pass != pass2 -> {
                            Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                        }
                        !UserRepo.canAddMore() -> {
                            Toast.makeText(context, "Capacidad de usuarios alcanzada (5).", Toast.LENGTH_SHORT).show()
                        }
                        UserRepo.addUser(User(user.trim(), pass)) -> {
                            Toast.makeText(context, "Usuario creado, inicia sesión 🎉", Toast.LENGTH_SHORT).show()
                            user = ""; email = ""; pass = ""; pass2 = ""
                            onBack() // Redirige al login
                        }
                        else -> {
                            Toast.makeText(context, "Usuario ya existe.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = isValid, // ⬅️ solo si está todo completo
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrar") }

            // Contador de cuentas restantes
            val restantes = 5 - UserRepo.users.size
            Text("Cuentas disponibles restantes: $restantes")
        }
    }
}
