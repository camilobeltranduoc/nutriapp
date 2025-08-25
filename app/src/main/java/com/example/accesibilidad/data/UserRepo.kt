package com.example.accesibilidad.data

import androidx.compose.runtime.mutableStateListOf

data class User(val username: String, val password: String)

object UserRepo {
    // Semilla de ejemplo (puedes borrarla si no quieres usuarios por defecto)
    val users = mutableStateListOf(
        User("ana",   "1234"),
        User("luis",  "1234")
    )

    private const val MAX_USERS = 5

    fun canAddMore(): Boolean = users.size < MAX_USERS

    /** Compara usuario sin distinguir mayúsculas/minúsculas y con trim en ambos campos */
    fun authenticate(username: String, password: String): Boolean {
        val u = username.trim()
        val p = password.trim()
        return users.any { it.username.equals(u, ignoreCase = true) && it.password == p }
    }

    /** Agrega evitando duplicados por nombre (case-insensitive) y con trim */
    fun addUser(user: User): Boolean {
        val u = user.username.trim()
        val p = user.password.trim()
        if (u.isBlank() || p.isBlank()) return false
        if (users.any { it.username.equals(u, ignoreCase = true) }) return false
        if (!canAddMore()) return false
        users.add(User(u, p))
        return true
    }

    /** Para “Recuperar clave” (demo). Devuelve null si no existe */
    fun getPassword(username: String): String? {
        val u = username.trim()
        return users.firstOrNull { it.username.equals(u, ignoreCase = true) }?.password
    }
}
