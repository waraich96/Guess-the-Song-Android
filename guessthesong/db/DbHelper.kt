package com.example.guessthesong.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_ALBUM
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_ARTIST
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_ATTEMPTS_LEFT
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_GENRE
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_ID
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_INDEX
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_IS_HALVED
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_IS_POINT_BOOSTED
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_IS_SOLVED
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_LENGTH
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_LYRIC
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_RELEASED
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_SONG
import com.example.guessthesong.db.LyricContract.Companion.COLUMN_TYPE
import com.example.guessthesong.db.LyricContract.Companion.TABLE_NAME

class DbHelper(context: Context?) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {


    companion object {
        private const val DATABASE_NAME = "guess.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val tableStatement =
            ("CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_INDEX + " INTEGER, "
                    + COLUMN_TYPE + " TEXT NOT NULL, "
                    + COLUMN_LYRIC + " TEXT NOT NULL, "
                    + COLUMN_ARTIST + " TEXT NOT NULL, "
                    + COLUMN_ALBUM + " TEXT NOT NULL, "
                    + COLUMN_SONG + " TEXT NOT NULL, "
                    + COLUMN_RELEASED + " TEXT NOT NULL, "
                    + COLUMN_GENRE + " TEXT NOT NULL, "
                    + COLUMN_LENGTH + " TEXT NOT NULL, "
                    + COLUMN_IS_SOLVED + " TEXT DEFAULT no, "
                    + COLUMN_IS_HALVED + " TEXT DEFAULT no, "
                    + COLUMN_IS_POINT_BOOSTED + " TEXT DEFAULT no, "
                    + COLUMN_ATTEMPTS_LEFT + " INTEGER DEFAULT 2 "
                    +");")

           sqLiteDatabase.execSQL(tableStatement)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(sqLiteDatabase)
    }

}
