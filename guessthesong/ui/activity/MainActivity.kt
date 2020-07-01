package com.example.guessthesong.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.guessthesong.R
import com.example.guessthesong.model.GameMode
import com.example.guessthesong.pref.GameSharedPreferences
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: GameSharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = GameSharedPreferences(this)
        initializeGameMode()
        buildButtonsListeners()

    }

    private fun initializeGameMode() {
        updateGameMode(gameModeSwitch.isChecked)
    }

    private fun buildButtonsListeners() {
        playButton.setOnClickListener { startGameActivity() }
        rulesButton.setOnClickListener { startRulesActivity() }
        gameModeSwitch.setOnCheckedChangeListener { _, isChecked -> updateGameMode(isChecked) }
    }

    private fun updateGameMode(checked: Boolean) {
        val mode =
            if (checked) GameMode.CURRENT
            else GameMode.CLASSIC
        sharedPref.setGameMode(mode)
    }

    private fun startRulesActivity() {
        val intent = Intent(this, RulesActivity::class.java)
        startActivity(intent)
    }

    private fun startGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }




}
