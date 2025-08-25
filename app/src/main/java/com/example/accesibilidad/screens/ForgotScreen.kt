package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.data.UserRepo
import com.example.accesibilidad.R
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotScreen(onBack: () -> Unit = {}) {
    var user by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
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
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val pwd = UserRepo.getPassword(user.trim())
                    msg = if (pwd != null) {
                        // Nota: en producción no se muestra la contraseña; esto es solo para la demo
                        "Tu contraseña registrada es: $pwd"
                    } else {
                        "Usuario no encontrado."
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                enabled = canRecover,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32), // verde accesible
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recuperar")
            }

            if (msg != null) {
                Text(msg!!)
            }
        }
    }
}
