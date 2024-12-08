package com.example.terpjump

import android.util.DisplayMetrics
import android.content.Context
import android.content.SharedPreferences
import android.graphics.RectF
import android.util.Log
import kotlin.random.Random

class Game(context: Context, highScore: Int) {
    private lateinit var player : Player
    private lateinit var platforms : ArrayList<Platform>
    private var score : Int = 0
    private var highScore : Int = highScore
    private var gameOver : Boolean = false

    private val screenWidth : Int
    private val screenHeight : Int

    init {
        // get screen size
        val metrics : DisplayMetrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        val platformSpacing = 200f
        var lastPlatformY = screenHeight.toFloat()
        // initialize 12 random platforms
        platforms = ArrayList()

        for(i in 0..12) {
            val p = Platform()
            if(i == 0) { // platform that the player will spawn on
                p.setX(450f)
                p.setY(2000f)
            } else {
                p.setX(Random.nextFloat() * (screenWidth - p.getWidth()))
                lastPlatformY -= platformSpacing
                p.setX(Random.nextFloat() * (screenWidth - p.getWidth()))
                p.setY(lastPlatformY)
            }
            addPlatform(p)
        }

        // initialize player
        player = Player()
    }

    fun getPlatforms() : ArrayList<Platform> {
        return this.platforms
    }

    fun addPlatform(p: Platform) {
        platforms.add(p)
    }

    fun getPlayer() : Player {
        return this.player
    }

    fun getScore() : Int {
        return this.score
    }

    fun setScore(score: Int, context: Context) {
        this.score = score
        if(score > this.highScore) {
            this.highScore = score
            updatePreferences(context)
        }
    }

    fun getHighScore() : Int {
        return highScore
    }

    fun gameOver() : Boolean {
        return gameOver
    }

    fun update(context: Context) {
        // Update platforms
        for (p in platforms) {
            val pX: Float = p.getX()
            val pY: Float = p.getY()
            val pWidth : Float = p.getWidth()
            val pHeight : Float = p.getHeight()

            // Update platform if it goes below the screen
            if (pY > screenHeight) {
                val randomOffset = Random.nextFloat() * 200f
                p.setY(0f - randomOffset) // reset to top
                p.setX(Random.nextInt(screenWidth - pWidth.toInt()).toFloat()) // randomize X position
            }
        }

        // Update player
        var playerX: Float = player.getX()
        var playerY: Float = player.getY()
        val jumpVelocity : Float = player.getJumpVelocity()
        val gravity : Float = player.getGravity()

        player.setJumpVelocity(jumpVelocity + gravity) // update jump acceleration
        playerY += jumpVelocity // update player Y position
        playerX += player.getMovement() // update player X position
//        playerX += 1

        // Check for collision with platforms
        for (p in platforms) {
            val pX: Float = p.getX()
            val pY: Float = p.getY()
            val pWidth: Float = p.getWidth()
            val pHeight: Float = p.getHeight()

            val playerRect = RectF(playerX, playerY, playerX + 100, playerY + 100)
            val platformRect = RectF(pX, pY, pX + pWidth, pY + pHeight)

            if (playerRect.intersect(platformRect) && jumpVelocity > 0) { // Check for overlap
                if (playerY + 100 <= pY + 20) { // Check if the player is landing on the platform (bottom collision)
                    player.setJumpVelocity(-40f) // player bounces up after collision
                    break
                }
            }
        }

        // If player crosses the middle of the screen, move platforms down
        val threshold = screenHeight / 2f // middle of the screen
        if (playerY < threshold) {
            val offset = threshold - playerY // how high the player jumped above the threshold
            setScore(this.score + offset.toInt(), context) // score
            playerY = threshold // keeps player below the threshold

            for(p in platforms) {
                p.movePlatformDown(offset) // moves platform down instead of moving the player up
            }
        }

        // End game if player goes below the screen
        if (playerY > screenHeight) {
            gameOver = true
        }

//        Log.d("DEBUG", gameOver.toString())

        // Screen wrapping
        if (playerX < -100) { // When player goes off left side
            playerX = screenWidth.toFloat()
        } else if (playerX > screenWidth) { // When player goes off right side
            playerX = -100f
        }

        player.setY(playerY)
        player.setX(playerX)
    }

    fun updatePreferences(context: Context) {
        var pref : SharedPreferences = context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()
        editor.putInt(HIGH_SCORE, highScore)
        editor.apply()
        editor.commit()
    }

    companion object {
        var HIGH_SCORE : String = "HIGH_SCORE"
    }
}