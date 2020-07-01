package com.example.guessthesong.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.guessthesong.R
import com.example.guessthesong.model.UserLyric

class LyricsAdapter (private val list: MutableList<UserLyric>,
                     private val listener: OnLyricClickListener) : RecyclerView.Adapter<LyricsAdapter.LyricViewHolder>() {


    interface OnLyricClickListener {
        fun onClick(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_item_lyric, parent, false)
        return LyricViewHolder(v)
    }

    override fun onBindViewHolder(holderLyric: LyricViewHolder, position: Int) {
        val lyric = list[position]
        holderLyric.bindView(lyric)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class LyricViewHolder(v: View)
        : RecyclerView.ViewHolder(v){

        var txtMsg: TextView = v.findViewById<View>(R.id.text_view) as TextView

        fun bindView(lyric: UserLyric){
            txtMsg.text = lyric.lyric
            txtMsg.setOnClickListener { listener.onClick(adapterPosition) }
        }

    }


}