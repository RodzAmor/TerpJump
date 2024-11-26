package com.example.terpjump

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// MainActivity handles the start screen
class MainActivity : AppCompatActivity() {
    private lateinit var startButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.start_button)
        startButton.setOnClickListener{ startGame() }
    }

    // Starts the game (starts activity_game view)
    fun startGame() {
        var intent : Intent = Intent(this, GameActivity::class.java)
        this.startActivity(intent)
    }
}