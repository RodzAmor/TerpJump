package com.example.terpjump

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// EndActivity handles the game over and leaderboard screen
class EndActivity : AppCompatActivity() {
    private lateinit var playAgainButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        playAgainButton = findViewById(R.id.play_again)
        playAgainButton.setOnClickListener{ playAgain() }
    }

    // Starts the game again (starts activity_game view)
    fun playAgain() {
        this.finish()
    }
}