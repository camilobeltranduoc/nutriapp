package com.example.accesibilidad.data


data class Recipe(
    val id: Int,
    val title: String,
    val category: String,   // "Desayuno", "Almuerzo", "Cena", "Snack"
    val calories: Int,
    val description: String
)
