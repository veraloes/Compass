package com.michalska.compass.utils

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.michalska.compass.feature.main.MainActivity
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.tan

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

    // TODO: 16/12/2020
    private fun getDirectionToDestination(
        currentLatitude: Double,
        currentLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double
    ): Double {
        val PI = Math.PI
        val dTeta =
            ln(tan(destinationLatitude / 2 + PI / 4) / tan(currentLatitude / 2 + PI / 4))
        val dLon = abs(currentLongitude - destinationLongitude)
        val teta = atan2(dLon, dTeta)
        return Math.round(Math.toDegrees(teta)).toDouble() //direction in degree
    }

    interface LocationListener {
        fun onGpsLocationChanged(latitude: String, longitude: String)
    }
}