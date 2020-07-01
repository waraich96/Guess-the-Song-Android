package com.example.guessthesong.utils

import android.database.Cursor
import com.example.guessthesong.db.LyricContract
import com.example.guessthesong.model.UserLyric


fun getUserLyricList(cursor: Cursor): MutableList<UserLyric>{
    val list = mutableListOf<UserLyric>()
    if(cursor.moveToFirst()){
        do {
            list.add(getUserLyricItem(cursor))
        } while (cursor.moveToNext())
    }
    return list
}


fun getUserLyricItem(cursor: Cursor): UserLyric {
    val id = cursor.getInt(cursor.getColumnIndex(LyricContract.COLUMN_ID))
    val index = cursor.getInt(cursor.getColumnIndex(LyricContract.COLUMN_INDEX))
    val lyric = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_LYRIC))
    val type = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_TYPE))
    val song = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_SONG))
    val album = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_ALBUM))
    val released = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_RELEASED))
    val length = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_LENGTH))
    val isSolved = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_IS_SOLVED))
    val attemptsLeft = cursor.getInt(cursor.getColumnIndex(LyricContract.COLUMN_ATTEMPTS_LEFT))
    val genre = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_GENRE))
    val artist = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_ARTIST))
    val isHalved = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_IS_HALVED))
    val isBoosted = cursor.getString(cursor.getColumnIndex(LyricContract.COLUMN_IS_POINT_BOOSTED))

    return UserLyric(id, index,type, lyric , artist, album, song, released, genre, length, isSolved, isHalved, isBoosted , attemptsLeft)
}