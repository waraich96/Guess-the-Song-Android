package com.example.guessthesong.ui.activity


import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.example.guessthesong.*
import com.example.guessthesong.db.LyricContract
import com.example.guessthesong.model.GameMode
import com.example.guessthesong.model.UserLyric
import com.example.guessthesong.ui.fragment.LyricsFragment
import com.example.guessthesong.ui.fragment.MapFragment
import com.example.guessthesong.ui.fragment.SongsFragment
import com.example.guessthesong.utils.getLyricDataListWithMode

import kotlinx.android.synthetic.main.activity_game.*

private const val SELECTED_POSITION = "selected_position"

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setUpBottomNavigationBar()

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_POSITION)) {
            val selectedItemId = savedInstanceState.getInt(SELECTED_POSITION)
            if (selectedItemId != 0) {
                setSelectedFragment(selectedItemId)
            } else {
                replaceFragment(LyricsFragment())
            }
        } else {
            replaceFragment(LyricsFragment())
        }
    }

    private fun setUpBottomNavigationBar() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            setSelectedFragment(item.itemId)
            true
        }
    }

    private fun setSelectedFragment(selectedItemId: Int) {
        when (selectedItemId) {
            R.id.lyricsMenu -> {
                replaceFragment(LyricsFragment())
            }
            R.id.mapMenu -> {
                replaceFragment(MapFragment())
            }
            R.id.songMenu -> {
                replaceFragment(SongsFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_POSITION, bottomNavigationView.selectedItemId)
    }
}
