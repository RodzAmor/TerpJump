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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// MainActivity handles the start screen
class MainActivity : AppCompatActivity() {
    private lateinit var startButton : Button
    private lateinit var highScoreTV: TextView
    private lateinit var doodlerChoices: RadioGroup
    private lateinit var doodlerSelection: RadioButton
    private lateinit var terrapinSelection: RadioButton
    private lateinit var coolterpSelection : RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        highScoreTV = findViewById(R.id.high_score_text)
        doodlerChoices = findViewById(R.id.doodler_selection_group)
        doodlerSelection = findViewById(R.id.original_doodler_radio)
        terrapinSelection = findViewById(R.id.terrapin_doodler_radio)
        coolterpSelection = findViewById(R.id.cool_terp_radio)
        startButton = findViewById(R.id.start_button)
        startButton.setOnClickListener{ startGame() }

        var pref : SharedPreferences = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()
        val highScore = pref.getInt(Game.HIGH_SCORE, 0)

        highScoreTV.text = highScore.toString()
        val selectedDoodler = pref.getString(DOODLER_PREFERENCE, "terrapin")
        // This loads the saved selection
        when (selectedDoodler) {
            "terrapin" -> terrapinSelection.isChecked = true
            "coolterp" -> coolterpSelection.isChecked = true
            else -> doodlerSelection.isChecked = true
        }

        // This sets the selection based on which one the user picks
        doodlerChoices.setOnCheckedChangeListener { _, checkedId ->
            val selection = when (checkedId) {
                R.id.terrapin_doodler_radio -> "terrapin"
                R.id.cool_terp_radio -> "coolterp"
                else -> "doodler"
            }

            editor.putString(DOODLER_PREFERENCE, selection)
            Log.w("MainActivity", "Doodler selected: " + selection)
            editor.apply()
        }

        var firebase : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = firebase.getReference("TerpJumpTest")
        reference.setValue("This works")

        // set up event handling
        var listener : DataListener = DataListener()
        reference.addValueEventListener(listener)
    }

    inner class DataListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.w("MainActivity", "Inside onDataChange")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("MainActivity", "error: " + error.toString())
        }
    }

    // Starts the game (starts activity_game view)
    fun startGame() {
        var intent : Intent = Intent(this, GameActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }

    companion object {
        const val DOODLER_PREFERENCE = "doodler_selection"
    }

}