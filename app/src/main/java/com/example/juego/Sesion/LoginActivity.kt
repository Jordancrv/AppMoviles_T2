package com.example.juego.Sesion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.juego.DB.DatabaseHelper
import com.example.juego.MainActivity
import com.example.juego.R
import com.example.juego.Sesion.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private lateinit var etUsernameOrEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar helpers
        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        // Inicializar vistas
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        // Botón de login
        btnLogin.setOnClickListener {
            val usernameOrEmail = etUsernameOrEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(usernameOrEmail, password)) {
                if (databaseHelper.loginPlayer(usernameOrEmail, password)) {
                    val player = databaseHelper.getPlayerData(usernameOrEmail)
                    if (player != null) {
                        sessionManager.createLoginSession(player.username, player.email, player.score)
                        Toast.makeText(this, "Bienvenido ${player.username}!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Navegar a registro
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInput(usernameOrEmail: String, password: String): Boolean {
        if (usernameOrEmail.isEmpty()) {
            etUsernameOrEmail.error = "Ingrese usuario o correo"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Ingrese contraseña"
            return false
        }
        return true
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
