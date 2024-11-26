package com.example.terpjump

import android.content.Context
import android.graphics.Canvas
import android.view.View

class GameView : View {
    constructor(context: Context, width: Int, height: Int) : super(context) {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw game view here

    }

    companion object {
        val DELTA_TIME : Int = 10
    }
}