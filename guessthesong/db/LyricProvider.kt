package com.example.guessthesong.db

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.example.guessthesong.db.LyricContract.Companion.TABLE_NAME

class LyricProvider : ContentProvider() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(): Boolean {
        dbHelper = DbHelper(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val database = dbHelper.readableDatabase
        val cursor =
            database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)

        if (context != null) cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null || values.size() == 0) {
            return null
        }
        val database = dbHelper.writableDatabase
        val id = database.insert(TABLE_NAME, null, values)

        return if (id == -1L) {
            null
        } else {
            if (context != null) context!!.contentResolver.notifyChange(uri, null)
            ContentUris.withAppendedId(uri, id)
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val database = dbHelper.writableDatabase
        val rowsUpdated = database.update(TABLE_NAME, values, selection, selectionArgs)

        if (rowsUpdated != 0 && context != null)
            context!!.contentResolver.notifyChange(uri, null)

        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val database = dbHelper.writableDatabase
        val rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs)
        if (rowsDeleted != 0 && context != null)
            context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}