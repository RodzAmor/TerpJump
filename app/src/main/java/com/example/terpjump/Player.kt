package com.example.terpjump

class Player {
    private var jumpVelocity : Float = 0f
    private var gravity : Float = 1f
    private var isJumping : Boolean = true
    private var x : Float = 480f
    private var y : Float = 1900f

    fun getX() : Float {
        return this.x
    }

    fun setX(x: Float) {
        this.x = x
    }

    fun getY() : Float {
        return this.y
    }

    fun setY(y: Float) {
        this.y = y
    }

    fun getJumpVelocity() : Float {
        return this.jumpVelocity
    }

    fun setJumpVelocity(jumpVelocity: Float) {
        this.jumpVelocity = jumpVelocity
    }

    fun getGravity() : Float {
        return this.gravity
    }

    fun isJumping() : Boolean {
        return this.isJumping
    }
}