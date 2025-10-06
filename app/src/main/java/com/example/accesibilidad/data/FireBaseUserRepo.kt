package com.example.accesibilidad.data.firebase

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.accesibilidad.data.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.UserProfileChangeRequest

object FirebaseUserRepo {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // ───────────────────────────────── Preferences (cache local de usuario)
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_UID = "user_uid"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_DISPLAY = "user_display_name"

    data class CachedUser(
        val uid: String,
        val email: String,
        val displayName: String?
    )

    private fun cacheCurrentUser(context: Context) {
        val u = auth.currentUser ?: return
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_UID, u.uid)
            .putString(KEY_EMAIL, u.email ?: "")
            .putString(KEY_DISPLAY, u.displayName)
            .apply()
    }

    fun readCachedUser(context: Context): CachedUser? {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val uid = prefs.getString(KEY_UID, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val display = prefs.getString(KEY_DISPLAY, null)
        return CachedUser(uid = uid, email = email, displayName = display)
    }

    fun clearCachedUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    // Si el usuario escribe "ana" lo convertimos a un email ficticio "ana@nutri.local"
    // Si ya viene con "@", se asume email real.
    private fun toEmail(usernameOrEmail: String): String {
        val u = usernameOrEmail.trim()
        return if (u.contains("@")) u else "$u@nutri.local"
    }

    val currentUserId: String? get() = auth.currentUser?.uid
    val currentEmail: String? get() = auth.currentUser?.email

    fun logout() = auth.signOut()

    /** Logout + limpiar cache local */
    fun logoutAndClear(context: Context) {
        logout()
        clearCachedUser(context)
    }

    /** Autenticación Firebase (true si inicia sesión ok) */
    suspend fun authenticate(context: Context, username: String, password: String): Boolean {
        val email = toEmail(username)
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            cacheCurrentUser(context) // ← guarda uid/email/displayName
            true
        } catch (_: Exception) {
            false
        }
    }

    /** Registro Firebase (true si crea ok; valida duplicados por email) */
    suspend fun addUser(context: Context, user: User): Boolean {
        val email = toEmail(user.username)
        val p = user.password.trim()
        if (email.isBlank() || p.isBlank()) return false
        return try {
            auth.createUserWithEmailAndPassword(email, p).await()
            // Opcional: establecer displayName si tu flujo lo maneja en otra parte
            cacheCurrentUser(context)
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Recuperar clave (reset por correo):
     *  - "RESET_SENT" si se envió
     *  - null si hubo error
     */
    suspend fun getPassword(username: String): String? {
        val email = toEmail(username)
        return try {
            auth.sendPasswordResetEmail(email).await()
            "RESET_SENT"
        } catch (_: Exception) {
            null
        }
    }

    /** Registro directo con email real (por si lo usas en otro flujo) */
    suspend fun register(email: String, password: String) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .await()
    }

    /** Enviar reset de clave a email o usuario simple (sin @) */
    suspend fun sendPasswordReset(emailOrUser: String) {
        val email = if (emailOrUser.contains("@")) emailOrUser.trim() else "${emailOrUser.trim()}@nutri.local"
        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .await()
    }

    // ───────────────────────────────
    // CRUD mínimo de USUARIO (Update + Delete)
    // ───────────────────────────────

    /** Reautenticación requerida por Firebase para operaciones sensibles */
    private suspend fun reauth(currentPassword: String) {
        val user = auth.currentUser ?: error("No hay sesión activa")
        val email = user.email ?: error("El usuario no tiene email")
        val cred = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(cred).await()
    }

    /** U: Cambiar contraseña (requiere reautenticación reciente) */
    suspend fun updatePassword(currentPassword: String, newPassword: String): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            reauth(currentPassword)
            user.updatePassword(newPassword.trim()).await()
            true
        } catch (_: Exception) {
            false
        }
    }

    /** D: Eliminar cuenta (requiere reautenticación reciente) */
    suspend fun deleteAccount(currentPassword: String): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            reauth(currentPassword)
            user.delete().await()
            true
        } catch (_: Exception) {
            false
        }
    }
    /** Actualiza el displayName del usuario y refresca el cache local */
    suspend fun updateDisplayName(context: Context, newName: String): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            val request = UserProfileChangeRequest.Builder()
                .setDisplayName(newName.trim())
                .build()
            user.updateProfile(request).await()
            // re-caché local
            cacheCurrentUser(context)
            true
        } catch (_: Exception) {
            false
        }
    }

    /** Forzar recacheo desde Firebase a SharedPreferences (por si lo necesitas) */
    fun recacheFromFirebase(context: Context) {
        cacheCurrentUser(context)
    }
}
