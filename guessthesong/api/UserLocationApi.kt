package com.example.guessthesong.api

import android.Manifest
import android.content.Context
import android.text.format.DateUtils
import com.example.guessthesong.ui.fragment.MapFragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class UserLocationApi(val listener: MapFragment.UserLocationListener) {

    companion object{
        private const val UPDATE_INTERVAL = 15 * DateUtils.SECOND_IN_MILLIS
        private const val FASTEST_INTERVAL = 5 * DateUtils.SECOND_IN_MILLIS
    }

    fun requestLocationUpdates(context: Context){
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest.interval = UPDATE_INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        try {
            LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(mLocationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            e.printStackTrace()

        }
    }

    fun stopLocationUpdates(context: Context) {
        LocationServices.getFusedLocationProviderClient(context)
            .removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            for (loc in result.locations) {
                if (loc != null) {
                    listener.onUpdateLocation(loc)
                }

            }
        }
    }
}