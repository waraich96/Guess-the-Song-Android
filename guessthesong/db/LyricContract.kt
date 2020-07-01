package com.example.guessthesong.db

import android.provider.BaseColumns

class LyricContract : BaseColumns{

    companion object{
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_INDEX = "indexing"
        const val COLUMN_TYPE = "type"
        const val COLUMN_LYRIC = "lyric"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_ALBUM = "album"
        const val COLUMN_SONG = "song"
        const val COLUMN_RELEASED = "released"
        const val COLUMN_GENRE = "genre"
        const val COLUMN_LENGTH = "length"
        const val COLUMN_IS_SOLVED = "is_solved"
        const val COLUMN_IS_HALVED = "is_halved"
        const val COLUMN_IS_POINT_BOOSTED = "is_point_boosted"
        const val COLUMN_ATTEMPTS_LEFT = "attempts_left"

        const val TABLE_NAME = "lyrics"
        const val CONTENT_AUTHORITY = "content://"+"com.example.guessthesong"
        const val AUTHORITY = "com.example.guessthesong"
    }

}