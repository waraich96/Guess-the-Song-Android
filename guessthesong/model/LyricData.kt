package com.example.guessthesong.model

import android.os.Parcelable
import com.google.android.gms.maps.model.Marker
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LyricData(
    val index : Int = -1,
    val lyric: String = "",
    val artist: String = "",
    val album: String = "",
    val song: String = "",
    val released: String = "",
    val genre: String = "",
    val length: String = "",
    var longitude: Double? = null,
    var latitude: Double? = null) : Parcelable