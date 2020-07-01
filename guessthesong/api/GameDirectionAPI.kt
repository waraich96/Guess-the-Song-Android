package com.example.guessthesong.api

import android.os.AsyncTask
import android.util.Log
import com.example.guessthesong.ui.fragment.MapFragment
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class GameDirectionAPI (listener: MapFragment.DirectionListener){
    private val asyncTask = DirectionAsyncTask(listener)
    fun getRoute(origin: LatLng, dest: LatLng){
        asyncTask.execute(origin, dest)
    }
}


    class DirectionAsyncTask(val listener: MapFragment.DirectionListener) : AsyncTask<LatLng, Void, String>() {

        companion object{
            private const val DIRECTION_API_REQUEST = "https://maps.googleapis.com/maps/api/directions/json"
            private const val GOOGLE_API_KEY = "AIzaSyBI5XkRkMpfnh_7QL4sZljWDYZxbs39Yig"
        }

        override fun doInBackground(vararg params: LatLng): String {
            var httpURLConnection: HttpURLConnection? = null
            var reader : BufferedReader? = null
            var result = ""
            val url = "$DIRECTION_API_REQUEST?origin=${params[0].latitude},${params[0].longitude}&destination=${params[1].latitude},${params[1].longitude}&key=$GOOGLE_API_KEY&mode=walking"
            try {
                httpURLConnection = URL(url).openConnection() as HttpURLConnection
                reader = httpURLConnection.inputStream.bufferedReader()
                result = reader.readText()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpURLConnection?.disconnect()
                reader?.close()
            }
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val list = mutableListOf<LatLng>()
                val jsonObj = JSONObject(result)
                val status = jsonObj.getString("status")
                Log.i("AddressApi", "result status : $status")
                val routesJsonArray = jsonObj.getJSONArray("routes")
                val firstObject = routesJsonArray.optJSONObject(0)
                if(firstObject!= null) {
                    val legs = firstObject.optJSONArray("legs")
                    if (legs != null)
                        for (x in 0 until legs.length()) {
                            val legObject = legs.getJSONObject(x)
                            val stepArray = legObject.optJSONArray("steps")
                            if (stepArray != null)
                                for (y in 0 until stepArray.length()) {

                                    val latS =
                                        stepArray.optJSONObject(y)?.optJSONObject("start_location")
                                            ?.optString("lat")
                                    val lngS =
                                        stepArray.optJSONObject(y)?.optJSONObject("start_location")
                                            ?.optString("lng")
                                    if (latS != null && lngS != null) {
                                        list.add(LatLng(latS.toDouble(), lngS.toDouble()))
                                    }

                                    val latE =
                                        stepArray.optJSONObject(y)?.optJSONObject("end_location")
                                            ?.optString("lat")
                                    val lngE =
                                        stepArray.optJSONObject(y)?.optJSONObject("end_location")
                                            ?.optString("lng")
                                    if (latE != null && lngE != null) {
                                        list.add(LatLng(latE.toDouble(), lngE.toDouble()))
                                    }
                                }
                        }
                }
                listener.onFetchedLatLng(list)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

}