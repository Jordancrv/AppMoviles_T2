package com.example.juego.Sesion

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.juego.DB.DatabaseHelper
import com.example.juego.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvScore: TextView
    private lateinit var ivQRCode: ImageView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvScore = findViewById(R.id.tvScore)
        ivQRCode = findViewById(R.id.ivQRCode)
        btnBack = findViewById(R.id.btnBack)

        loadUserProfile()

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadUserProfile() {
        val userDetails = sessionManager.getUserDetails()
        val username = userDetails["username"] ?: return

        val player = databaseHelper.getPlayerData(username)

        if (player != null) {
            tvUsername.text = "Usuario: ${player.username}"
            tvEmail.text = "Email: ${player.email}"
            tvScore.text = "Puntaje Máximo: ${player.maxScore}"

            // Generar y mostrar QR
            player.qrToken?.let { token ->
                val qrBitmap = QRHelper.generateQRCode(token, 400)
                if (qrBitmap != null) {
                    ivQRCode.setImageBitmap(qrBitmap)
                } else {
                    Toast.makeText(this, "Error generando QR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
