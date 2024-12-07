package com.example.terpjump

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.View
import kotlin.random.Random


class GameView : View {
    private lateinit var game : Game
    private lateinit var paint : Paint
    private var width : Int = 0
    private var height : Int = 0

    private val playerBitmap : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.doodle_left)
    private val platformBitmap : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.blue_platform)

    constructor (context : Context, width : Int, height : Int) : super(context) {
        this.width = width
        this.height = height

        // set local persistent data for high score
        var pref : SharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        var highScore = pref.getInt(Game.HIGH_SCORE, 0)

        // initialize game
        game = Game(context, highScore)

        // set paint attributes
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.strokeWidth = 20f
        paint.textSize = 60f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPlatforms(canvas)
        drawPlayer(canvas)
        drawScore(canvas)
    }

    fun drawPlatforms(canvas: Canvas) {
        for (p in game.getPlatforms()) {
            val pX: Float = p.getX()
            val pY: Float = p.getY()
            val pWidth : Float = p.getWidth()
            val pHeight : Float = p.getHeight()

//            canvas.drawRect(pX, pY, pX + pWidth, pY + pHeight, paint)
            val platformBitmap : Bitmap = Bitmap.createScaledBitmap(platformBitmap, pWidth.toInt(), pHeight.toInt(), false)
//            var platformBitmap : Bitmap = Bitmap.createBitmap(platformBitmap, pX.toInt(), pY.toInt(), (pX + pWidth).toInt(), (pY + pWidth).toInt())
            canvas.drawBitmap(platformBitmap, pX, pY, paint)

        }
    }

    fun drawPlayer(canvas: Canvas) {
        val player = game.getPlayer()
        val playerX: Float = player.getX()
        var playerY: Float = player.getY()

//        canvas.drawRect(playerX, playerY, playerX + 100, playerY + 100, paint)
        canvas.drawBitmap(playerBitmap, playerX, playerY, paint)
    }

    fun drawScore(canvas: Canvas) {
        val score = game.getScore()
        canvas.drawText("Score: " + score, 50f, 100f, paint) // Draw text on canvas
    }

    fun getGame() : Game {
        return game
    }

    companion object {
        val DELTA_TIME : Int = 10
    }
}