package com.example.terpjump

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

// EndActivity handles the game over and leaderboard screen
class EndActivity : AppCompatActivity() {
    private lateinit var playAgainButton : Button
    private lateinit var adView : AdView
    private lateinit var scoreTV : TextView
    private lateinit var highscoreTV : TextView
    private lateinit var homeButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        scoreTV = findViewById(R.id.score)
        highscoreTV = findViewById(R.id.high_score)
        playAgainButton = findViewById(R.id.play_again)
        homeButton = findViewById(R.id.home)

        playAgainButton.setOnClickListener{ playAgain() }
        homeButton.setOnClickListener{ home() }

        setupAd()
        updateScores()

        var pref : SharedPreferences = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()
        val highScore = pref.getInt(Game.HIGH_SCORE, 0)

        // Prevents the screen rotation during accelerometer testing
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    fun updateScores() {
        val score : Int = getIntent().getIntExtra("SCORE", 0)
        val highscore : Int = getIntent().getIntExtra("HIGH_SCORE", 0)
        scoreTV.text = "Score: " + score.toString()
        highscoreTV.text = "Highscore: " + highscore.toString()
    }

    // Starts the game again (starts activity_game view)
    fun playAgain() {
        val intent : Intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Go back to the home screen
    fun home() {
        val intent : Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun setupAd() {
        var adSize : AdSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        var adUnitId : String = "ca-app-pub-3940256099942544/6300978111"
        adView = AdView(this)
        adView.setAdSize(adSize)
        adView.adUnitId = adUnitId

        var builder : AdRequest.Builder = AdRequest.Builder()
        builder.addKeyword("game")
        var request : AdRequest = builder.build()

        var adLayout : LinearLayout = findViewById(R.id.ad_view)
        adLayout.addView(adView)

        adView.loadAd(request)
    }
}