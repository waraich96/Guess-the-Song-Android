package com.example.guessthesong.utils

import android.content.Context
import android.provider.SyncStateContract
import android.util.Log
import com.example.guessthesong.model.GameMode
import com.example.guessthesong.model.LyricData
import org.json.JSONArray
import java.io.IOException

fun getLyricDataListWithMode(context: Context, mode: GameMode): MutableList<LyricData>{
    val fileName =
        when (mode){
            GameMode.CLASSIC -> "classic.json"
            GameMode.CURRENT -> "current.json"
        }
    return getLyricListFromFile(context, fileName)
}

private fun getLyricListFromFile(context: Context, fileName: String): MutableList<LyricData> {
    val list = mutableListOf<LyricData>()
    val json = getAssetJsonData(context, fileName) ?: return list
    val jsonArray = JSONArray(json)
    for(x in 0 until jsonArray.length()){
        val jsonObject = jsonArray.getJSONObject(x)
        val lyric = LyricData(
            x,
            jsonObject.optString(LYRIC),
            jsonObject.optString(ARTIST),
            jsonObject.optString(ALBUM),
            jsonObject.optString(SONG),
            jsonObject.optString(RELEASED),
            jsonObject.optString(GENRE),
            jsonObject.optString(LENGTH)
        )
        list.add(lyric)
    }
    return list
}

private fun getAssetJsonData(context: Context, fileName: String): String? {
    val json: String
    try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.use { it.read(buffer) }
        json = String(buffer)
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return json
}
