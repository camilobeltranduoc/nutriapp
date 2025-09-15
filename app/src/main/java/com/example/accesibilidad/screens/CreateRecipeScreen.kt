package com.example.accesibilidad.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.RecipeRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    onBack: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Desayuno") }
    var caloriesText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var titleErr by remember { mutableStateOf<String?>(null) }
    var calErr by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Desayuno", "Almuerzo", "Cena", "Snack")

    fun validate(): Boolean {
        val t = title.trim()
        val kcal = caloriesText.trim().toIntOrNull()
        titleErr = if (t.isBlank()) "Título requerido" else null
        calErr = when {
            caloriesText.isBlank() -> "Calorías requeridas"
            kcal == null -> "Debe ser un número"
            kcal <= 0 -> "Debe ser mayor que 0"
            else -> null
        }
        return titleErr == null && calErr == null
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Crear receta",
                centerIconRes = R.drawable.icon,
                onBack = onBack,
                onSettings = null
            )
        },
        bottomBar = {
            // CTA fijo abajo – grande y accesible
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        if (!RecipeRepo.canAddMore()) {
                            val msg = "Capacidad máxima de recetas alcanzada"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (ttsEnabled) speak(msg)
                            return@Button
                        }

                        if (!validate()) {
                            val err = listOfNotNull(titleErr, calErr).firstOrNull()
                                ?: "Completa los campos requeridos"
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            if (ttsEnabled) speak(err)
                            return@Button
                        }

                        val added = RecipeRepo.addRecipe(
                            title.trim(),
                            category.trim(),
                            caloriesText.trim().toIntOrNull() ?: 0,
                            description.trim()
                        )
                        val msg = if (added) "Receta creada correctamente"
                        else "Ya existe una receta con ese título"

                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (ttsEnabled) speak(msg)
                        if (added) onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = title.isNotBlank() && caloriesText.isNotBlank()
                ) {
                    Text("Guardar receta")
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HERO
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Nueva receta",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        "Completa los campos. Usa un título claro y registra las calorías estimadas.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // FORMULARIO
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; if (titleErr != null) titleErr = null },
                        label = { Text("Título") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Título"
                            )
                        },
                        isError = titleErr != null,
                        supportingText = {
                            Text(
                                titleErr ?: "Ej: Ensalada de quinoa",
                                color = if (titleErr != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Categoría", style = MaterialTheme.typography.labelLarge)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories.size) { i ->
                            val c = categories[i]
                            FilterChip(
                                selected = category == c,
                                onClick = {
                                    category = c
                                    if (ttsEnabled) speak("Categoría $c seleccionada")
                                },
                                label = { Text(c) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.heightIn(min = 48.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = {
                            caloriesText = it.filter { ch -> ch.isDigit() }.take(4)
                            if (calErr != null) calErr = null
                        },
                        label = { Text("Calorías") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Calorías"
                            )
                        },
                        isError = calErr != null,
                        supportingText = {
                            Text(
                                calErr ?: "Solo números (kcal)",
                                color = if (calErr != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it.take(200) },
                        label = { Text("Descripción") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Descripción"
                            )
                        },
                        supportingText = {
                            Text("${description.length}/200")
                        },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // RESUMEN / CUPOS
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                val restantes = 20 - RecipeRepo.recipes.size
                ListItem(
                    headlineContent = { Text("Recetas disponibles restantes") },
                    trailingContent = {
                        Text(
                            "$restantes/20",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }

            Spacer(Modifier.height(80.dp)) // espacio para que el botón inferior no tape campos
        }
    }
}
