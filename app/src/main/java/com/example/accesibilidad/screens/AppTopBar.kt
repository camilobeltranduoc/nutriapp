package com.example.accesibilidad.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AppTopBar(
    title: String? = null,                 // Texto a la IZQUIERDA
    @DrawableRes centerIconRes: Int,       // Logo/ícono CENTRADO
    onBack: (() -> Unit)? = null,          // Flecha si no es null
    onSettings: (() -> Unit)? = null       // Botón ajustes derecha (opcional)
) {
    Surface(
        color = Color(0xFF2E7D32),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {

            // IZQUIERDA: back + título, limitado para no chocar con el centro
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.6f) // 👈 reserva solo 60% del ancho para el lado izquierdo
                    .padding(start = 4.dp, end = 8.dp)
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(Modifier.width(12.dp))
                }
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 1,                           // 👈 no se parte en 2 líneas
                        overflow = TextOverflow.Ellipsis        // 👈 si no alcanza, pone …
                    )
                }
            }

            // CENTRO: logo (ligeramente más pequeño)
            Image(
                painter = painterResource(id = centerIconRes),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp) // 👈 antes 36dp; así gana un poco más de aire
            )

            // DERECHA: ajustes (opcional)
            if (onSettings != null) {
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
