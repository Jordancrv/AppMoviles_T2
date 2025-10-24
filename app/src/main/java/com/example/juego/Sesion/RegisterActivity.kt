package com.example.juego.Sesion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.juego.DB.DatabaseHelper
import com.example.juego.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)

        // Inicializar vistas
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        // Botón de registro
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInput(username, email, password, confirmPassword)) {
                if (databaseHelper.checkUserExists(username, email)) {
                    Toast.makeText(this, "El usuario o email ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    // Cambio aquí: comparar con -1L en lugar de usar como Boolean
                    if (databaseHelper.registerPlayer(username, email, password) != -1L) {
                        Toast.makeText(this, "Registro exitoso! Inicia sesión", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Navegar a login
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInput(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty()) {
            etUsername.error = "Ingrese nombre de usuario"
            return false
        }
        if (username.length < 3) {
            etUsername.error = "Usuario debe tener al menos 3 caracteres"
            return false
        }
        if (email.isEmpty()) {
            etEmail.error = "Ingrese correo electrónico"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Correo inválido"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Ingrese contraseña"
            return false
        }
        if (password.length < 6) {
            etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }
        return true
    }
}
