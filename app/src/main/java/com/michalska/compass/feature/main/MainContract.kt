package com.michalska.compass.feature.main

import android.app.Activity
import android.widget.EditText
import com.michalska.compass.base.BasePresenterInterface

interface MainContract {

    interface View {
        fun showUpdatedLocation(latitude: String, longitude: String)
        fun startLocation()
        fun displayError(latitudeError: String, longitudeError: String)
        fun getViewActivity(): Activity
        fun invalidLongitude()
        fun invalidLatitude()
        fun clearLongitude()
        fun clearLatitude()
        fun runSwipeUp()
        fun loadDestination()
        fun displayDestinationLocation(latitude: String, longitude: String)
        fun setDistance(): Float
    }

    interface Presenter :
        BasePresenterInterface<View> {
        fun locationChanged(latitude: String, longitude: String)
        fun requestPermission()
        fun onPermissionResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        )

        fun setLongitude(setLongitude: EditText)
        fun setLatitude(setLatitude: EditText)
        fun handleButtonClick()
        fun handleSaveButtonClick()
        fun locationDestinationChanged(latitude: String, longitude: String)
        fun getDistanceInKm(
            currentLatitude: Float,
            currentLongitude: Float,
            destinationLatitude: Float,
            destinationLongitude: Float
        ): Float
    }
}