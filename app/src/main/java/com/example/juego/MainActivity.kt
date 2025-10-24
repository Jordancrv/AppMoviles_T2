package com.example.juego

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.juego.Sesion.LoginActivity
import com.example.juego.Sesion.SessionManager
import com.example.juego.DB.DatabaseHelper

import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var score = 0
    var imageArray = ArrayList<ImageView>()
    var handler = Handler(Looper.getMainLooper())
    var runnable = Runnable {  }

    private lateinit var timeText: TextView
    private lateinit var scoreText: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SessionManager y DatabaseHelper ANTES de setContentView
        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)

        // Log para debug
        Log.d("MainActivity", "MainActivity iniciada")
        val isLogged = sessionManager.isLoggedIn()
        Log.d("MainActivity", "Sesión activa: $isLogged")

        // Verificar si hay sesión activa ANTES de setContentView
        if (!isLogged) {
            Log.d("MainActivity", "Redirigiendo a LoginActivity")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        timeText= findViewById<TextView>(R.id.timeText)
        scoreText= findViewById<TextView>(R.id.scoreText)

        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.pikachu)
        val imageView2: ImageView = findViewById(R.id.imageView2)
        imageView2.setImageResource(R.drawable.pikachu)
        val imageView3: ImageView = findViewById(R.id.imageView3)
        imageView3.setImageResource(R.drawable.pikachu)
        val imageView4: ImageView = findViewById(R.id.imageView4)
        imageView4.setImageResource(R.drawable.pikachu)
        val imageView5: ImageView = findViewById(R.id.imageView5)
        imageView5.setImageResource(R.drawable.pikachu)
        val imageView6: ImageView = findViewById(R.id.imageView6)
        imageView6.setImageResource(R.drawable.pikachu)
        val imageView7: ImageView = findViewById(R.id.imageView7)
        imageView7.setImageResource(R.drawable.pikachu)
        val imageView8: ImageView = findViewById(R.id.imageView8)
        imageView8.setImageResource(R.drawable.pikachu)
        val imageView9: ImageView = findViewById(R.id.imageView9)
        imageView9.setImageResource(R.drawable.pikachu)

        //ImageArray
        imageArray.add(imageView)
        imageArray.add(imageView2)
        imageArray.add(imageView3)
        imageArray.add(imageView4)
        imageArray.add(imageView5)
        imageArray.add(imageView6)
        imageArray.add(imageView7)
        imageArray.add(imageView8)
        imageArray.add(imageView9)

        hideImages()

        //CountDown Timer
        object : CountDownTimer(15500,1000){
            override fun onFinish() {
                timeText.text = "Tiempo: 0 seg"
                handler.removeCallbacks(runnable)
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }

                // Guardar puntaje final
                val userDetails = sessionManager.getUserDetails()
                val username = userDetails["username"]
                if (username != null) {
                    val currentScore = userDetails["score"]?.toIntOrNull() ?: 0
                    if (score > currentScore) {
                        databaseHelper.updateScore(username, score)
                        sessionManager.updateScore(score)
                        Log.d("MainActivity", "Puntaje guardado: $score para usuario: $username")
                    }
                }

                val alert = AlertDialog.Builder(this@MainActivity)
                alert.setTitle("Juego terminado")
                alert.setMessage("Tu puntaje: $score\n¿Reiniciar el juego?")
                alert.setPositiveButton("Si") {dialog, which ->
                    val intent = intent
                    finish()
                    startActivity(intent)
                }
                alert.setNegativeButton("No") {dialog, which ->
                    Toast.makeText(this@MainActivity,"Juego Terminado =/",Toast.LENGTH_LONG).show()
                }
                alert.setNeutralButton("Cerrar Sesión") {dialog, which ->
                    sessionManager.logoutUser()
                    Log.d("MainActivity", "Sesión cerrada")
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                alert.show()
            }

            override fun onTick(millisUntilFinished: Long) {
                timeText.text = "Tiempo: " + millisUntilFinished/1000 + " seg"
            }

        }.start()
    }

    fun hideImages() {
        runnable = object : Runnable {
            override fun run() {
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }
                val random = Random()
                val randomIndex = random.nextInt(9)
                imageArray[randomIndex].visibility = View.VISIBLE
                handler.postDelayed(runnable,500)
            }
        }
        handler.post(runnable)
    }

    fun increaseScore(view: View){
        score = score + 1
        scoreText.text = "Puntaje: $score"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        Log.d("MainActivity", "MainActivity destruida")
    }
}
