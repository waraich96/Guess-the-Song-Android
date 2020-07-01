package com.example.guessthesong.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.guessthesong.model.GameMode

private const val GAME_MODE_TAG = "game_mode_tag"
private const val POINTS_TAG = "points_tag"
private const val POWER_TAG = "power_tag"
private const val COLLECTED_LYRICS_COUNTER_TAG = "collected_lyrics_counter_tag"
private const val SUCCESS_ATTEMPTS_COUNTER_TAG = "success_attempts_counter_tag"

class GameSharedPreferences(context: Context) {

    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getGameMode() = sharedPreferences.getString(GAME_MODE_TAG, GameMode.CLASSIC.name)

    fun setGameMode(mode : GameMode){
        sharedPreferences.edit().putString(GAME_MODE_TAG, mode.name).apply()
    }

    fun getUserPoints() = sharedPreferences.getInt(POINTS_TAG, 0)

    fun setUserPoints(value: Int){
        sharedPreferences.edit().putInt(POINTS_TAG, value).apply()
    }

    fun getUserPower() = sharedPreferences.getInt(POWER_TAG, 3)

    fun setUserPower(value: Int){
        sharedPreferences.edit().putInt(POWER_TAG, value).apply()
    }

    fun getUserSuccessAttemptsCount() = sharedPreferences.getInt(SUCCESS_ATTEMPTS_COUNTER_TAG, 0)

    fun setUserSuccessAttemptsCount(value: Int){
        sharedPreferences.edit().putInt(SUCCESS_ATTEMPTS_COUNTER_TAG, value).apply()
    }

    fun getUserCollectedLyricsCount() = sharedPreferences.getInt(COLLECTED_LYRICS_COUNTER_TAG, 0)

    fun setUserCollectedLyricsCount(value: Int){
        sharedPreferences.edit().putInt(COLLECTED_LYRICS_COUNTER_TAG, value).apply()
    }

    fun updateUserCollectedLyricsByOne(isCollected: Boolean){
        var counter = getUserCollectedLyricsCount()

        if(isCollected)
            counter++
        else
            counter--

        if(counter< 0) counter = 0
        setUserCollectedLyricsCount(counter)
    }
}