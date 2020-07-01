package  com.example.guessthesong.ui.activity

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.guessthesong.R
import com.example.guessthesong.db.LyricContract
import com.example.guessthesong.model.GameMode
import com.example.guessthesong.model.LyricData
import com.example.guessthesong.model.UserLyric
import com.example.guessthesong.pref.GameSharedPreferences
import com.example.guessthesong.utils.NO
import com.example.guessthesong.utils.YES
import com.example.guessthesong.utils.getLyricDataListWithMode
import kotlinx.android.synthetic.main.activity_guess_song.*
import kotlinx.android.synthetic.main.dialog_congrats_layout.view.*


const val SELECTED_USER_LYRIC = "selected_user_lyric"
const val SAVED_LYRIC_TAG = "user_lyric"
const val SPINNER_CHOICE_LIST_TAG = "spinner_choice_list_tag"
const val IS_CORRECT_DIALOG_DISPLAYED = "is_correct_dialog_displayed"
const val IS_FAILED_DIALOG_DISPLAYED = "is_failed_dialog_displayed"

class GuessSongActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "GuessSongActivity"
        private const val DELIMITER = " - "
    }

    private lateinit var sharedPreferences: GameSharedPreferences
    private var userLyric: UserLyric? = null
    private lateinit var mode: GameMode
    private var lyricsDataList: MutableList<LyricData>? = null
    private var power: Int? = null
    private var choiceList: ArrayList<String>? = null
    private var rightAnswer: String = ""
    private var alertDialog: AlertDialog? = null
    private var isCorrectDialogDisplayed = false
    private var isFailedDialogDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess_song)
        getSelectedSong()
        restoreGameState(savedInstanceState)
        sharedPreferences = GameSharedPreferences(this)
        getGameMode()
        getLyricsDataListOfGameMode()
        buildUI()
        buildUserData()
        buildButtonListeners()
        buildSpinner()
    }

    private fun restoreGameState(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        choiceList = savedInstanceState.getStringArrayList(SPINNER_CHOICE_LIST_TAG)
        if(savedInstanceState.containsKey(SAVED_LYRIC_TAG)){
            userLyric = savedInstanceState.getParcelable(SAVED_LYRIC_TAG)
        }
        isCorrectDialogDisplayed = savedInstanceState.getBoolean(IS_CORRECT_DIALOG_DISPLAYED)
        isFailedDialogDisplayed = savedInstanceState.getBoolean(IS_FAILED_DIALOG_DISPLAYED)

        if(isCorrectDialogDisplayed)
            displayCongratsDialog()
        else if(isFailedDialogDisplayed)
            displayFailureDialog()
    }

    private fun getGameMode() {
        mode = com.example.guessthesong.utils.getGameMode(sharedPreferences.getGameMode())
    }

    private fun getSelectedSong() {
        intent ?: return
        userLyric = intent.getParcelableExtra(SELECTED_USER_LYRIC)
        rightAnswer = userLyric!!.song + DELIMITER + userLyric!!.artist
    }

    private fun getLyricsDataListOfGameMode() {
        lyricsDataList = getLyricDataListWithMode(this, mode)
    }


    private fun buildUI() {
        userLyric ?: return
        lyricTextView.text = userLyric!!.lyric
        numberOfAttemptText.text = getString(R.string.number_of_attempts, userLyric!!.attemptsLeft)
        if (userLyric!!.isPointBoosted == YES)
            pointsCountImg.setImageResource(R.drawable.points100)
    }

    private fun buildUserData() {
        power = sharedPreferences.getUserPower()
        val points = sharedPreferences.getUserPoints()
        val collectedLyrics = sharedPreferences.getUserCollectedLyricsCount()

        powerTextView.text = getString(R.string.power, power)
        pointsTextView.text = getString(R.string.your_points, points)
        collectedLyricsTextView.text = getString(R.string.lyrics_collected, collectedLyrics)
    }

    private fun buildSpinner() {
        userLyric ?: return
        if (choiceList == null) {
            val numberOfChoices = if (userLyric!!.isHalved == YES) 3 else 6
            val allItemsList = generateSongAndArtistList()
            choiceList = getChoiceListWithRightIndex(rightAnswer, numberOfChoices, allItemsList)
        }
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, choiceList!!.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.isSaveEnabled = true
    }

    private fun halveSpinnerList() {
        choiceList ?: return
        val numberOfChoices = 3
        choiceList = getChoiceListWithRightIndex(rightAnswer, numberOfChoices, choiceList!!)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, choiceList!!.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun boostPoints() {
        pointsCountImg.setImageResource(R.drawable.points100)
    }

    private fun buildButtonListeners() {
        returnButton.setOnClickListener { finish() }
        halveText.setOnClickListener { useHalvePower() }
        pointBoost.setOnClickListener { useBoostPointsPower() }
        completeImg.setOnClickListener { checkUserAnswer() }
    }

    private fun useBoostPointsPower() {
        userLyric ?: return
        if (userLyric!!.isPointBoosted == YES) {
            Toast.makeText(this, R.string.already_boosted, Toast.LENGTH_LONG).show()
        } else if (power == null || power == 0) {
            Toast.makeText(this, R.string.have_no_power, Toast.LENGTH_LONG).show()
        } else {
            userLyric!!.isPointBoosted = YES
            val values = ContentValues()
            values.put(LyricContract.COLUMN_IS_POINT_BOOSTED, YES)
            updateUserLyricLocally(values)
            sharedPreferences.setUserPower(power!! - 1)
            boostPoints()
        }
        buildUserData()
    }

    private fun useHalvePower() {
        userLyric ?: return
        if (userLyric!!.isHalved == YES) {
            Toast.makeText(this, R.string.already_halved, Toast.LENGTH_LONG).show()
        } else if (power == null || power == 0) {
            Toast.makeText(this, R.string.have_no_power, Toast.LENGTH_LONG).show()
        } else {
            userLyric!!.isHalved = YES
            val values = ContentValues()
            values.put(LyricContract.COLUMN_IS_HALVED, YES)
            updateUserLyricLocally(values)
            sharedPreferences.setUserPower(power!! - 1)
            halveSpinnerList()
        }
        buildUserData()
    }

    private fun checkUserAnswer(){
        if(spinner.selectedItem as String == rightAnswer)
            markUserLyricAsSolved()
        else
            markAttemptAsFailed()

        buildUserData()
    }
    private fun markUserLyricAsSolved(){
        userLyric ?: return
        userLyric!!.isSolved = YES
        val points = if(userLyric!!.isPointBoosted == YES) 100 else 50
        sharedPreferences.setUserPoints(sharedPreferences.getUserPoints() + points)
        val values = ContentValues()
        values.put(LyricContract.COLUMN_IS_SOLVED, YES)
        updateUserLyricLocally(values)
        sharedPreferences.setUserPower(power!! + 1)
        displayCongratsDialog()
    }

    private fun markAttemptAsFailed(){
        userLyric ?: return
        val attempts = userLyric!!.attemptsLeft
        userLyric!!.attemptsLeft = attempts - 1
        if(attempts - 1 > 0 ){
            val values = ContentValues()
            values.put(LyricContract.COLUMN_ATTEMPTS_LEFT, attempts - 1)
            values.put(LyricContract.COLUMN_IS_HALVED, NO)
            values.put(LyricContract.COLUMN_IS_POINT_BOOSTED, NO)
            pointsCountImg.setImageResource(R.drawable.points50)
            numberOfAttemptText.text = getString(R.string.number_of_attempts, userLyric!!.attemptsLeft)
            updateUserLyricLocally(values)
            displayFailureDialog()
        } else {
            removeUserLyric()
            displayFailureDialog()
        }
    }

    private fun displayFailureDialog() {
        alertDialog?.dismiss()
        val view = layoutInflater.inflate(R.layout.dialog_congrats_layout, parent_view , false )
        view.correct.text = getString(R.string.attempt_failed)
        view.correct.setTextColor(ContextCompat.getColor(this, R.color.md_red_700))
        view.message.text = getString(R.string.incorrect_answer)
        view.image.visibility = View.GONE
        view.closeButton.setOnClickListener {
            if(userLyric != null && userLyric!!.attemptsLeft == 0)
                finish()
            alertDialog?.dismiss()
            isFailedDialogDisplayed = false
        }
        view.okButton.setOnClickListener {
            if(userLyric != null && userLyric!!.attemptsLeft == 0)
                finish()
            alertDialog?.dismiss()
            isFailedDialogDisplayed = false
        }
        alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        alertDialog?.show()
        isFailedDialogDisplayed = true
    }

    private fun displayCongratsDialog() {
        alertDialog?.dismiss()
        val view = layoutInflater.inflate(R.layout.dialog_congrats_layout, parent_view , false )
        view.closeButton.setOnClickListener {
            finish()
            alertDialog?.dismiss()
            isCorrectDialogDisplayed = false
        }
        view.okButton.setOnClickListener {
            finish()
            alertDialog?.dismiss()
            isCorrectDialogDisplayed = false
        }
        alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        alertDialog?.show()
        isCorrectDialogDisplayed = true
    }

    private fun removeUserLyric(){
        userLyric ?: return
        val where = LyricContract.COLUMN_ID + " =?"
        val arg = arrayOf(userLyric!!.id.toString())
        val deletedRows = contentResolver.delete(Uri.parse(LyricContract.CONTENT_AUTHORITY),where, arg )
        Log.i(TAG, "deleteUserLyricLocally rows: $deletedRows")
        if(deletedRows> 0) {
            sharedPreferences.setUserCollectedLyricsCount(sharedPreferences.getUserCollectedLyricsCount() - 1)
        }
    }

    private fun updateUserLyricLocally(values: ContentValues) {
        userLyric ?: return
        val where = LyricContract.COLUMN_ID + " =?"
        val arg = arrayOf(userLyric!!.id.toString())
        val updatedRows = contentResolver.update(Uri.parse(LyricContract.CONTENT_AUTHORITY), values, where, arg )
        Log.i(TAG, "updateUserLyricLocally rows: $updatedRows")
    }

    private fun generateSongAndArtistList(): MutableList<String> {
        val list = mutableListOf<String>()
        lyricsDataList ?: return list
        lyricsDataList!!.forEach { lyricData ->
            val text = lyricData.song + DELIMITER + lyricData.artist
            list.add(text)
        }
        return list
    }

    private fun getChoiceListWithRightIndex(
        rightAnswer: String,
        numberOfChoices: Int,
        parentList: MutableList<String>
    ): ArrayList<String> {
        val list = arrayListOf<String>()
        list.add(rightAnswer)
        do {
            val random = parentList.random()
            if (!list.contains(random)) list.add(random)
        } while (list.size < numberOfChoices)
        list.shuffle()
        return list
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(SPINNER_CHOICE_LIST_TAG, choiceList)
        outState.putParcelable(SAVED_LYRIC_TAG, userLyric)
        outState.putBoolean(IS_CORRECT_DIALOG_DISPLAYED, isCorrectDialogDisplayed)
        outState.putBoolean(IS_FAILED_DIALOG_DISPLAYED, isFailedDialogDisplayed)
    }
}