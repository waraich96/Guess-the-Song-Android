package  com.example.guessthesong.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guessthesong.R
import com.example.guessthesong.model.UserLyric
import com.example.guessthesong.pref.GameSharedPreferences
import com.example.guessthesong.utils.getImage
import kotlinx.android.synthetic.main.activity_song_info.*
import kotlinx.android.synthetic.main.fragment_lyrics.collectedLyricsTextView
import kotlinx.android.synthetic.main.fragment_lyrics.powerTextView

class SongInfoActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: GameSharedPreferences
    private var userLyric: UserLyric? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_info)
        sharedPreferences = GameSharedPreferences(this)
        getSelectedSong()
        buildUI()
        buildUserData()
        buildReturnButton()
    }


    private fun getSelectedSong() {
        intent ?: return
        userLyric = intent.getParcelableExtra(SELECTED_USER_LYRIC)
    }

    private fun buildUI(){
        userLyric ?: return
        lyricTextView.text = userLyric!!.lyric
        artist.text = userLyric!!.artist
        album.text = userLyric!!.album
        song.text = userLyric!!.song
        songTitleTextView.text = userLyric!!.song
        released.text = userLyric!!.released
        length.text = userLyric!!.length
        genre.text = userLyric!!.genre
        albumImage.setImageResource(getImage(userLyric!!.song))
    }

    private fun buildUserData() {
        val power = sharedPreferences.getUserPower()
        val points = sharedPreferences.getUserPoints()
        val collectedLyrics = sharedPreferences.getUserCollectedLyricsCount()

        powerTextView.text = getString(R.string.power, power)
        pointsTextView.text = getString(R.string.your_points, points)
        collectedLyricsTextView.text = getString(R.string.lyrics_collected, collectedLyrics)
    }

    private fun buildReturnButton(){
        returnButton.setOnClickListener { finish() }
    }

}