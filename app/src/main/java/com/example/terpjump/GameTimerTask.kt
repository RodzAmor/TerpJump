package com.example.terpjump

import java.util.TimerTask

// GameTimerTask is a task that will be scheduled by a Timer object
class GameTimerTask : TimerTask {
    private lateinit var activity : GameActivity
    private lateinit var game : Game

    constructor(activity: GameActivity) {
        this.activity = activity
    }

    override fun run() {
        activity.updateModel()
        activity.updateView()
    }
}