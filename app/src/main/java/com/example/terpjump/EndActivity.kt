package com.example.terpjump

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

// EndActivity handles the game over and leaderboard screen
class EndActivity : AppCompatActivity() {
    private lateinit var playAgainButton : Button
    private lateinit var adView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        playAgainButton = findViewById(R.id.play_again)
        playAgainButton.setOnClickListener{ playAgain() }

        setupAd()

        // Prevents the screen rotation during accelerometer testing
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    // Starts the game again (starts activity_game view)
    fun playAgain() {
        val intent : Intent = Intent(this, GameActivity::class.java)
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