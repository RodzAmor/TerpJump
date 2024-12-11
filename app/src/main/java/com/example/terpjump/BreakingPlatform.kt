package com.example.terpjump

import android.graphics.RectF

class BreakingPlatform : Platform() {
    private var isBroken : Boolean = false

    fun handleCollision(playerX : Float, playerY : Float, playerWidth : Float,
                        playerHeight : Float, jumpVelocity : Float) : Boolean {
        val playerRect = RectF(playerX, playerY,
            playerX + playerWidth, playerY + playerHeight)
        val platformRect = RectF(getX(), getY(), getX() + getWidth(),
            getY() + getHeight())

        var isIntersecting : Boolean = playerRect.intersect(platformRect)
        var isFalling : Boolean = jumpVelocity > 0
        // 20 is for padding
        var isLandingOnTop : Boolean = playerY + playerHeight <= getY() + 20

        if (isIntersecting && isFalling && isLandingOnTop) {
            if (!isBroken) {
                isBroken = true
                return true
            }
        }

        return false
    }

    fun isBroken() : Boolean {
        return isBroken
    }
}