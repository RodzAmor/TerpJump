package com.example.terpjump

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

// EndActivity handles the game over and leaderboard screen
class EndActivity : AppCompatActivity() {
    private lateinit var gameOverLayout : LinearLayout
    private lateinit var playAgainButton : Button
    private lateinit var adView : AdView
    private lateinit var scoreTV : TextView
    private lateinit var highscoreTV : TextView
    private lateinit var homeButton : Button
    private lateinit var playerInputLayout : LinearLayout
    private lateinit var playerNameET : EditText
    private lateinit var starRating : RatingBar
    private lateinit var submitButton : Button
    private lateinit var launcher : ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)
        playerInputLayout = findViewById<LinearLayout>(R.id.player_input_layout)
        gameOverLayout = findViewById<LinearLayout>(R.id.game_over_layout)

        scoreTV = findViewById(R.id.score)
        highscoreTV = findViewById(R.id.high_score)
        playAgainButton = findViewById(R.id.play_again)
        homeButton = findViewById(R.id.home)
        submitButton = findViewById(R.id.submit_button)
        starRating = findViewById(R.id.rating_bar)
        playerNameET = findViewById<EditText>(R.id.player_name)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.w("EndActivity", "Location Permission Granted")
            location = fetchLocation()
        } else {
            Log.w("EndActivity", "Location Permission Not Granted, ask for it")
            var contract : ActivityResultContracts.RequestPermission =
                ActivityResultContracts.RequestPermission()
            var results : Results = Results()
            launcher = registerForActivityResult(contract, results)
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        submitButton.setOnClickListener{ submitScore() }
        playAgainButton.setOnClickListener{ playAgain() }
        homeButton.setOnClickListener{ home() }

        var pref : SharedPreferences = getSharedPreferences("game_preferences",
            Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()
        val highScore = pref.getInt(Game.HIGH_SCORE, 0)

        // Prevents the screen rotation during accelerometer testing
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    private fun fetchLocation() : String {
        Log.w("EndActivityDebug", "runs")
        var latitude = 0.0
        var longitude = 0.0
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                Log.w("EndActivity", "Lat: $latitude, Long: $longitude")
            } else {
                Log.w("EndActivity", "Failed to get location")
            }
        }.addOnFailureListener {
            Log.e("EndActivity", "Error getting location: ${it.message}")
        }
        return "Lat: $latitude, Long: $longitude"
    }

    inner class Results : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean) {
            if (result) {
                Log.w("MainActivity", "User gave permission")
            } else {
                Log.w("MainActivity", "User permission denied")
                Toast.makeText(this@EndActivity, "Location permission is required",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun submitScore() {
        var name = playerNameET.text.toString()
        var rating = starRating.rating

        if (name.isNotBlank()) {
            // send all data to firebase, including score
            val score : Int = getIntent().getIntExtra("SCORE", 0)
            Log.w("EndActivity", "Name: $name, Rating: $rating, Score: $score, " +
                    "Location: $location")

            // Layout transition
            playerInputLayout.visibility = View.GONE
            gameOverLayout.visibility = View.VISIBLE

            updateScores()
            setupAd()
        } else {
            playerNameET.error = "Please enter a name"
        }
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