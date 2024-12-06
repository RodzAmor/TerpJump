package com.example.terpjump

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import android.content.SharedPreferences

// GameActivity handles the game screen
class GameActivity : AppCompatActivity() {
    private lateinit var gameView : GameView
    private lateinit var game : Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        buildViewByCode()

        if(game.gameOver()) {
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

    fun buildViewByCode() {
        var width : Int = resources.displayMetrics.widthPixels
        var height : Int = resources.displayMetrics.heightPixels

        val rectangle : Rect = Rect(0, 0, 0, 0)
        window.decorView.getWindowVisibleDisplayFrame(rectangle)

        gameView = GameView(this, width, height - rectangle.top)
        game = gameView.getGame()
        setContentView(gameView)
    }

    fun updateView() {
        gameView.postInvalidate()
    }

    fun updateModel() {
        game.update(this)
    }
}