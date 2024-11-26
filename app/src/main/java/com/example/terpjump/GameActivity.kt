package com.example.terpjump

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer

// GameActivity handles the game screen
class GameActivity : AppCompatActivity() {
    private lateinit var gameView : GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        // game over if player dies?
        if(true) {
            gameOver()
        }
    }

    // Called when the user is active on the app, starts task/animation
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        var timer : Timer = Timer()
        var task : GameTimerTask = GameTimerTask(this)
        timer.schedule(task, 0L, GameView.DELTA_TIME.toLong())
    }

    // Ends the game (starts activity_end view)
    fun gameOver() {
        var intent : Intent = Intent(this, EndActivity::class.java)
        this.startActivity(intent)
    }

    fun updateView() {
        gameView.postInvalidate()
    }

    fun updateModel() {
        // update model here
    }
}