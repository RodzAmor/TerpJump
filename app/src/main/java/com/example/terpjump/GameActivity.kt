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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.graphics.rotationMatrix

// GameActivity handles the game screen
class GameActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var gameView : GameView
    private lateinit var game : Game
    private lateinit var timer : Timer
    private lateinit var sensorManager : SensorManager
    private lateinit var accelerometer : Sensor
//    private var accelerometer : Sensor? =  null
//    private var gyroscope : Sensor? = null
//    private var rotationSensor : Sensor? = null
    private var tiltX : Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        buildViewByCode()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        // Prevents the screen rotation during accelerometer testing
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    // Called when the user is active on the app, starts task/animation
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        var timer : Timer = Timer()
//        var task : GameTimerTask = GameTimerTask(this)
//        timer.schedule(task, 0L, GameView.DELTA_TIME.toLong())
//        timer.scheduleAtFixedRate(task, 0L, GameView.DELTA_TIME.toLong())
//    }

    // Ends the game (starts activity_end view)
    fun gameOver() {
        var intent : Intent = Intent(this, EndActivity::class.java)
        this.startActivity(intent)

        finish()
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

        var timer : Timer = Timer()
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
//                Log.d("GameActivity", "TiltX: " + (-it.values[0]).toString())
                tiltX = -it.values[0] // Invert X to match natural movement
                game.getPlayer().setMovement(tiltX)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used but needed to override
    }
}