package com.example.juego.Sesion

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val PREF_NAME = "PokemonGameSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_SCORE = "score"
    }

    // Crear sesión de usuario
    fun createLoginSession(username: String, email: String, score: Int) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        editor.putInt(KEY_SCORE, score)
        editor.apply()
    }

    // Verificar si el usuario está logueado
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Obtener datos del usuario
    fun getUserDetails(): HashMap<String, String?> {
        return hashMapOf(
            KEY_USERNAME to prefs.getString(KEY_USERNAME, null),
            KEY_EMAIL to prefs.getString(KEY_EMAIL, null),
            KEY_SCORE to prefs.getInt(KEY_SCORE, 0).toString()
        )
    }

    // Cerrar sesión
    fun logoutUser() {
        editor.clear()
        editor.apply()
    }

    // Actualizar puntaje en sesión
    fun updateScore(score: Int) {
        editor.putInt(KEY_SCORE, score)
        editor.apply()
    }
}
