package com.example.guessthesong.model

import android.os.Parcel
import android.os.Parcelable
import com.example.guessthesong.utils.NO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLyric(
    var id: Int = -1,
    val index: Int = -1,
    val type: String = "",
    val lyric: String = "",
    val artist: String = "",
    val album: String = "",
    val song: String = "",
    val released: String = "",
    val genre: String = "",
    val length: String = "",
    var isSolved: String = NO,
    var isHalved: String = NO,
    var isPointBoosted: String = NO,
    var attemptsLeft: Int = 2
) : Parcelable