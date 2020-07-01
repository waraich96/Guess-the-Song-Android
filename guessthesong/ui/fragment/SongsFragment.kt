package com.example.guessthesong.ui.fragment


import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guessthesong.R
import com.example.guessthesong.db.LyricContract
import com.example.guessthesong.model.GameMode
import com.example.guessthesong.model.UserLyric
import com.example.guessthesong.pref.GameSharedPreferences
import com.example.guessthesong.ui.activity.SELECTED_USER_LYRIC
import com.example.guessthesong.ui.activity.SongInfoActivity
import com.example.guessthesong.ui.adapter.SongsAdapter
import com.example.guessthesong.utils.YES
import com.example.guessthesong.utils.getUserLyricList
import kotlinx.android.synthetic.main.fragment_lyrics.*
import kotlinx.android.synthetic.main.fragment_lyrics.view.*


/**
 * A simple [Fragment] subclass.
 */

private const val LOADER_ID = 78
private const val RECYCLER_VIEW_STATE_KEY = "recycler_view_state"

class SongsFragment : Fragment() , LoaderManager.LoaderCallbacks<Cursor> {

    private var adapter: SongsAdapter? = null
    private var list = mutableListOf<UserLyric>()
    private var cursor: Cursor? = null
    private var recyclerViewState: Parcelable? = null
    private lateinit var mode: GameMode
    private lateinit var sharedPreferences: GameSharedPreferences

    private val clickListener = object : SongsAdapter.OnSongClickListener{
        override fun onClick(pos: Int) {
            startSongInfoActivity(list[pos])
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lyrics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = GameSharedPreferences(requireContext())
        getGameMode()
        queryUserCollectedLyrics()
        buildToolbar()
        buildRecyclerView()
    }

    private fun getGameMode() {
        mode = com.example.guessthesong.utils.getGameMode(sharedPreferences.getGameMode())
    }

    private fun queryUserCollectedLyrics() {
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, this)
    }

    private fun buildToolbar() {
        toolbar.title = getString(R.string.songs)
    }

    private fun buildRecyclerView() {
        adapter = SongsAdapter(list, clickListener)
        recyclerView.apply {
            layoutManager = GridLayoutManager(activity , 1)
            recyclerView.layoutManager = layoutManager
            val divider = DividerItemDecoration(
                recyclerView.context,
                RecyclerView.VERTICAL
            )
            recyclerView.addItemDecoration(divider)
            recyclerView.setHasFixedSize(true)
        }
    }

    private fun buildUserData() {
        val power = sharedPreferences.getUserPower()
        val points = sharedPreferences.getUserPoints()
        val collectedLyrics = sharedPreferences.getUserCollectedLyricsCount()

        powerTextView.text = getString(R.string.power, power)
        pointsTextView.text = getString(R.string.your_points, points)
        collectedLyricsTextView.text = getString(R.string.lyrics_collected, collectedLyrics)
    }

    private fun updateAdapter(cursor: Cursor?) {
        cursor ?: return
        list = getUserLyricList(cursor)
        adapter = SongsAdapter(list, clickListener);
        recyclerView.adapter = adapter;
        if (recyclerViewState != null && recyclerView.layoutManager != null) {
            recyclerView.layoutManager!!.onRestoreInstanceState(recyclerViewState);
        }
    }

    private fun startSongInfoActivity(userLyric: UserLyric) {
        val intent = Intent(requireActivity(), SongInfoActivity::class.java)
        intent.putExtra(SELECTED_USER_LYRIC, userLyric)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        queryUserCollectedLyrics()
        buildUserData()
        buildToolbar()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState ?: return
        if (savedInstanceState.containsKey(RECYCLER_VIEW_STATE_KEY)) {
            recyclerViewState =
                savedInstanceState.getParcelable(RECYCLER_VIEW_STATE_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (recyclerView.layoutManager != null) {
            recyclerViewState = recyclerView.layoutManager!!.onSaveInstanceState()
            outState.putParcelable(RECYCLER_VIEW_STATE_KEY, recyclerViewState)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = Uri.parse(LyricContract.CONTENT_AUTHORITY)
        val selection: String =
            LyricContract.COLUMN_TYPE + "=? AND " + LyricContract.COLUMN_IS_SOLVED+ "=?"
        val selectionArgs = arrayOf(mode.name, YES)
        return CursorLoader(requireContext(), uri, null, selection, selectionArgs, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        cursor = data
        updateAdapter(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}
