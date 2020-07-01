package com.example.guessthesong.api


import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.example.guessthesong.utils.isConnected
import com.google.android.gms.maps.model.LatLng
import java.util.*


class AddressApi {
    fun getLatLngFromAddress(address: String, context : Context): LatLng?{
        return if(isConnected(context))
        {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 2)
            return if(addresses.isNotEmpty()) {
                val add = addresses[0]
                LatLng(add.latitude, add.longitude)
            } else{
                null
            }
        } else {
            null
        }
    }
}