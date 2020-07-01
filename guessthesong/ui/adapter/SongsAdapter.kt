package com.example.guessthesong.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.guessthesong.R
import com.example.guessthesong.model.LyricData
import com.example.guessthesong.model.UserLyric
import com.example.guessthesong.pref.GameSharedPreferences
import com.example.guessthesong.utils.getImage

class SongsAdapter(private val list: MutableList<UserLyric>,
                   private val listener: OnSongClickListener) : RecyclerView.Adapter<SongsAdapter.SongsViewHolder>() {

    interface OnSongClickListener {
        fun onClick(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_item_song, parent, false)
        return SongsViewHolder(v)
    }

    override fun onBindViewHolder(holderLyric: SongsViewHolder, position: Int) {
        val lyric = list[position]
        holderLyric.bindView(lyric)
    }

    override fun getItemCount(): Int {
        return list.size
    }



    inner class SongsViewHolder(v: View)
        : RecyclerView.ViewHolder(v){

        var txtMsg: TextView = v.findViewById<View>(R.id.text_view) as TextView
        var albumImage: ImageView = v.findViewById<View>(R.id.albumImage) as ImageView

        fun bindView(lyric: UserLyric){
            txtMsg.text = (lyric.artist + " : " + lyric.song)
            albumImage.setImageResource(getImage(lyric.song))

            txtMsg.setOnClickListener { listener.onClick(adapterPosition) }
        }
    }

}