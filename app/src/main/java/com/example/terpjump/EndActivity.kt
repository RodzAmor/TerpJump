package com.example.terpjump

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
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

        submitButton.setOnClickListener{ submitScore() }
        playAgainButton.setOnClickListener{ playAgain() }
        homeButton.setOnClickListener{ home() }

        var pref : SharedPreferences = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()
        val highScore = pref.getInt(Game.HIGH_SCORE, 0)

    }

    fun submitScore() {
        var name = playerNameET.text.toString()
        var rating = starRating.rating

        if (name.isNotBlank()) {
            // send all data to firebase, including score
            val score : Int = getIntent().getIntExtra("SCORE", 0)
            Log.w("EndActivity", "Name: $name, Rating: $rating, Score: $score, Location:")

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
                        for (scoreSnapshot in snapshot.children) {
                            val name = scoreSnapshot.child("name").getValue(String::class.java)
                            val score = scoreSnapshot.child("score").getValue(Int::class.java)
                            val rating = scoreSnapshot.child("rating").getValue(Float::class.java)

                            Log.d("DEBUG", name + score.toString() + rating.toString())
                            // We can also change how the leaderboard is displayed
                            leaderboard += "${name} : ${score} : ${rating} \n"
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