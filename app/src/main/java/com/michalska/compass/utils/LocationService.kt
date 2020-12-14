package com.michalska.compass.utils

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.michalska.compass.feature.main.MainActivity

class LocationService(
    private val listener: MainActivity,
    private val locationManager: LocationManager
) : LocationListener {

    init {
        initLocation()
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() {
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        location?.let { onLocationChanged(it) }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()
        listener.onGpsLocationChanged(latitude, longitude)
    }

    interface LocationListener {
        fun onGpsLocationChanged(latitude: String, longitude: String)
    }
}