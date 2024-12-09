package com.example.terpjump

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.renderscript.Sampler.Value
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
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.installations.installations

// EndActivity handles the game over and leaderboard screen
class EndActivity : AppCompatActivity() {
    private lateinit var gameOverLayout : LinearLayout
    private lateinit var playAgainButton : Button
    private lateinit var adView : AdView
    private lateinit var gameScoreTV : TextView
    private lateinit var scoreTV : TextView
    private lateinit var highscoreTV : TextView
    private lateinit var leaderboardTV : TextView
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
        gameScoreTV = findViewById(R.id.player_score)
        playerInputLayout = findViewById<LinearLayout>(R.id.player_input_layout)
        gameOverLayout = findViewById<LinearLayout>(R.id.game_over_layout)

        scoreTV = findViewById(R.id.score)
        highscoreTV = findViewById(R.id.high_score)
        leaderboardTV = findViewById(R.id.leaderboard)
        playAgainButton = findViewById(R.id.play_again)
        homeButton = findViewById(R.id.home)
        submitButton = findViewById(R.id.submit_button)
        starRating = findViewById(R.id.rating_bar)
        playerNameET = findViewById<EditText>(R.id.player_name)

        // Display score in the input screen
        val score : Int = getIntent().getIntExtra("SCORE", 0)
        gameScoreTV.text = "Score: " + score.toString()

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

            submitToFirebase(name, rating, score)
            loadLeaderboard()

            updateScores()
            setupAd()
        } else {
            playerNameET.error = "Please enter a name"
        }
    }

    fun submitToFirebase(name : String, rating : Float, score : Int) {
        val database = FirebaseDatabase.getInstance()
        val scoresRef = database.getReference("scores")
        val newScoreRef = scoresRef.push()

        val scoreData = mapOf(
            "name" to name,
            "score" to score,
            "rating" to rating
        )

        newScoreRef.setValue(scoreData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DEBUG", "Firebase entry successful. Check the database")
            } else {
                Log.d("DEBUG", "Firebase entry not successful")
            }
        }
    }

    fun updateScores() {
        val score : Int = getIntent().getIntExtra("SCORE", 0)
        val highscore : Int = getIntent().getIntExtra("HIGH_SCORE", 0)
        scoreTV.text = "Score: " + score.toString()
        highscoreTV.text = "Highscore: " + highscore.toString()
    }

    fun loadLeaderboard() {
        val database = FirebaseDatabase.getInstance()
        val scoresRef = database.getReference("scores")

        scoresRef.orderByChild("score").limitToFirst(10) // Only getting 10
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var leaderboard : String = "Name : Score : Rating \n"
                    if (snapshot.exists()) {
                        var leaderboardEntries = mutableListOf<Triple<String, Int, Float>>()

                        for (scoreSnapshot in snapshot.children) {
                            val name = scoreSnapshot.child("name").getValue(String::class.java) ?: ""
                            val score = scoreSnapshot.child("score").getValue(Int::class.java) ?: 0
                            val rating = scoreSnapshot.child("rating").getValue(Float::class.java) ?: 0f

//                            Log.d("DEBUG", name + score.toString() + rating.toString())
                            // We can also change how the leaderboard is displayed
//                            leaderboard += "${name} : ${score} : ${rating} \n"

                            leaderboardEntries.add(Triple(name, score, rating))
                        }

                        leaderboardEntries.sortByDescending { it.second }

                        for (leaderboardEntry in leaderboardEntries) {
                            leaderboard += "${leaderboardEntry.first} : ${leaderboardEntry.second}" +
                                    " : ${leaderboardEntry.third} \n"
                        }
                    } else {
                        Log.d("DEBUG", "Snapshot does not exist")
                    }

                    Log.d("DEBUG", "leaderboard: " + leaderboard)
                    leaderboardTV.text = leaderboard
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DEBUG", "Failed to load leaderboard ${error.message}")
                }
            })
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