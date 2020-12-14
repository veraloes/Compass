package com.michalska.compass.utils

import android.location.Location
import android.location.LocationManager
import org.junit.Test

class GPSPollingServiceTest {

    private val locationManager: LocationManager? = null

    @Test
    fun testGPS() {

        locationManager?.setTestProviderEnabled("Test", true)

        // Set up your test
        val location = Location("Test")
        location.latitude = 10.0
        location.longitude = 20.0
        locationManager?.setTestProviderLocation("Test", location)

        // Check if your listener reacted the right way
        locationManager?.removeTestProvider("Test")
    }
}
