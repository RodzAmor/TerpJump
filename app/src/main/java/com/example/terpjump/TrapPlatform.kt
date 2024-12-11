package com.example.terpjump

import android.graphics.RectF

// Extends platform
// If player steps on this, then it is game over
class TrapPlatform : Platform() {
    fun checkCollision(playerX : Float, playerY : Float, playerWidth : Float,
                       playerHeight : Float, jumpVelocity : Float) : Boolean {
        val playerRect = RectF(playerX, playerY,
                        playerX + playerWidth, playerY + playerHeight)
        val platformRect = RectF(getX(), getY(), getX() + getWidth(),
                        getY() + getHeight())

        var isIntersecting : Boolean = playerRect.intersect(platformRect)
        var isFalling : Boolean = jumpVelocity > 0
        // 20 is for padding
        var isLandingOnTop : Boolean = playerY + playerHeight <= getY() + 20

        return isIntersecting && isFalling && isLandingOnTop
    }
}