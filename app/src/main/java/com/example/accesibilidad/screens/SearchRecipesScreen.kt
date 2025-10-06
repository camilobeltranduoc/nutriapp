package com.example.accesibilidad.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.accesibilidad.R
import com.example.accesibilidad.data.RecipeRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRecipesScreen(
    onBack: () -> Unit = {},
    ttsEnabled: Boolean = false,
    speak: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>("Todas") }
    var maxCaloriesText by remember { mutableStateOf("") }
    val snack = remember { SnackbarHostState() }

    val categories = remember { listOf("Todas", "Desayuno", "Almuerzo", "Cena", "Snack") }
    val maxCalories = maxCaloriesText.toIntOrNull()

    // ----- Lanzadores para voz -----
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val spoken = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = spoken?.firstOrNull().orEmpty()
            if (text.isNotBlank()) {
                query = text
                if (ttsEnabled) speak("Buscando $text")
            }
        }
    }

    fun startSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }
        speechLauncher.launch(intent)
    }

    val results by remember(query, selectedCategory, maxCaloriesText) {
        mutableStateOf(
            RecipeRepo.search(
                query = query,
                category = selectedCategory?.takeIf { it != "Todas" },
                maxCalories = maxCalories
            )
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Buscar recetas",
                centerIconRes = R.drawable.icon,
                onBack = onBack,
                onSettings = null
            )
        },
        snackbarHost = { SnackbarHost(snack) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ====== BÚSQUEDA ======
            ElevatedCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Búsqueda",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.semantics { heading() }
                        )
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Por nombre o descripción") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { startSpeech() }) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Buscar por voz"
                                )
                            }
                        }
                    )
                }
            }

            // ====== FILTROS ======
            ElevatedCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Filtros",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.semantics { heading() }
                        )
                    }

                    Text("Categoría", style = MaterialTheme.typography.labelLarge)

                    FlowRowMainAxisSpaced {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = (selectedCategory ?: "Todas") == cat,
                                onClick = {
                                    selectedCategory = cat
                                    if (ttsEnabled) speak("Categoría $cat seleccionada")
                                },
                                label = { Text(cat) },
                                modifier = Modifier.heightIn(min = 48.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = maxCaloriesText,
                        onValueChange = {
                            maxCaloriesText = it.filter { ch -> ch.isDigit() }.take(4)
                        },
                        label = { Text("Calorías máximas (opcional)") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ====== RESULTADOS ======
            Text(
                "Resultados: ${results.size}",
                style = MaterialTheme.typography.labelLarge
            )

            ElevatedCard {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Encabezado
                    item {
                        ListItem(
                            headlineContent = {
                                Text("Receta", style = MaterialTheme.typography.titleSmall)
                            },
                            supportingContent = {
                                Text("Categoría", style = MaterialTheme.typography.titleSmall)
                            },
                            trailingContent = {
                                Text("kcal", style = MaterialTheme.typography.titleSmall)
                            }
                        )
                        Divider()
                    }

                    // Filas
                    items(results) { r ->
                        ListItem(
                            headlineContent = { Text(r.title) },
                            supportingContent = { Text(r.category) },
                            trailingContent = { Text("${r.calories}") }
                        )
                        Divider()
                    }

                    if (results.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay resultados para los filtros actuales")
                            }
                        }
                    }
                }
            }

            // Feedback por voz al filtrar
            LaunchedEffect(results.size) {
                if (ttsEnabled) {
                    if (results.isEmpty()) speak("Sin resultados para los filtros actuales")
                    else speak("Se encontraron ${results.size} recetas")
                }
            }
        }
    }
}

/** Helper simple para distribuir chips sin dependencias externas */
@Composable
private fun FlowRowMainAxisSpaced(content: @Composable RowScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { content() }
    }
}
