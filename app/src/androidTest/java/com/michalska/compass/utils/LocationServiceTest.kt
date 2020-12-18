package com.michalska.compass.utils

import android.location.Location
import android.location.LocationManager
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class GPSPollingServiceTest {

    @Mock
    private val locationManager: LocationManager? = null

    @Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

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
