package com.example.terpjump

class Platform {
    private var width : Float = 150f
    private var height : Float = 15f
    private var x : Float = 0f
    private var y : Float = 0f

    fun getHeight() : Float {
        return this.height
    }

    fun setHeight(height: Float) {
        this.height = height
    }

    fun getWidth() : Float {
        return this.width
    }

    fun setWidth(width: Float) {
        this.width = width
    }

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

    fun movePlatformDown(offset: Float) {
        setY(getY() + offset) // move platform down
    }
}