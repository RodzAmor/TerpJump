package com.example.terpjump

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdView

// MainActivity handles the start screen
class MainActivity : AppCompatActivity() {
    private lateinit var startButton : Button
    private lateinit var highScoreTV: TextView
    private lateinit var doodlerChoices: RadioGroup
    private lateinit var doodlerSelection: RadioButton
    private lateinit var terrapinSelection: RadioButton
    private lateinit var adView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        highScoreTV = findViewById(R.id.high_score_text)
        doodlerChoices = findViewById(R.id.doodler_selection_group)
        doodlerSelection = findViewById(R.id.original_doodler_radio)
        terrapinSelection = findViewById(R.id.terrapin_doodler_radio)
        startButton = findViewById(R.id.start_button)
        startButton.setOnClickListener{ startGame() }

        val pref = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()
        val highScore = pref.getInt(HIGH_SCORE, 0)

        highScoreTV.text = highScore.toString()
        val selectedDoodler = pref.getString(DOODLER_PREFERENCE, "terrapin")
        // This loads the saved selection
        when (selectedDoodler) {
            "doodler" -> doodlerSelection.isChecked = true
            else -> terrapinSelection.isChecked = true
        }

        // This sets the selection based on which one the user picks
        doodlerChoices.setOnCheckedChangeListener { _, checkedId ->
            val selection = when (checkedId) {
                R.id.terrapin_doodler_radio -> "terrapin"
                else -> "original"
            }

            editor.putString(DOODLER_PREFERENCE, selection)
            Log.w("MainActivity", "Doodler selected: " + selection)
            editor.apply()
        }
    }

    // Starts the game (starts activity_game view)
    fun startGame() {
        var intent : Intent = Intent(this, GameActivity::class.java)
        this.startActivity(intent)
    }

    companion object {
        const val HIGH_SCORE = "high_score"
        const val DOODLER_PREFERENCE = "doodler_selection"
    }

}