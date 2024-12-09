package com.example.terpjump

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.rotationMatrix

// GameActivity handles the game screen
class GameActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var gameView : GameView
    private lateinit var progressBar : ProgressBar
    private lateinit var game : Game
    private lateinit var timer : Timer
    private lateinit var sensorManager : SensorManager
    private lateinit var accelerometer : Sensor
    private var tiltX : Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        buildViewByCode()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
    }

    // Ends the game (starts activity_end view)
    fun gameOver() {
        var intent : Intent = Intent(this, EndActivity::class.java)
        intent.putExtra("SCORE", game.getScore())
        intent.putExtra("HIGH_SCORE", game.getHighScore())
        this.startActivity(intent)

        finish()
    }

    fun buildViewByCode() {
        var width : Int = resources.displayMetrics.widthPixels
        var height : Int = resources.displayMetrics.heightPixels

        val rectangle : Rect = Rect(0, 0, 0, 0)
        window.decorView.getWindowVisibleDisplayFrame(rectangle)

        val rootLayout = RelativeLayout(this)

        gameView = GameView(this, width, height - rectangle.top)
        game = gameView.getGame()
        rootLayout.addView(gameView)

        // Add ProgressBar
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        progressBar.max = game.getHighScore()
        progressBar.progress = game.getScore()
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, // width
            50 // y
        )
        progressBar.layoutParams = layoutParams
        rootLayout.addView(progressBar)

        setContentView(rootLayout)
    }

    fun updateView() {
        gameView.postInvalidate()
        progressBar.progress = game.getScore()
    }

    fun updateModel() {
        game.update(this)

        if (game.gameOver()) {
            gameOver()
        }
    }

    // Sensor Event Listener functions
    override fun onResume() {
        super.onResume()

        accelerometer.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        timer = Timer()
        var task : GameTimerTask = GameTimerTask(this)
        timer.schedule(task, 0L, GameView.DELTA_TIME.toLong())
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onSensorChanged(event : SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                tiltX = -it.values[0] // Invert X to match natural movement
                game.getPlayer().setMovement(tiltX)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used but needed to override
    }
}