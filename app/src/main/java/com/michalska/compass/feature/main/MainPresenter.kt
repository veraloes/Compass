package com.michalska.compass.feature.main

import android.Manifest
import android.content.pm.PackageManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.michalska.compass.R
import com.michalska.compass.base.BasePresenter
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.sqrt


class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {

    private var longitude: EditText? = null
    private var latitude: EditText? = null
    private var currentLatitude: Float = 0f
    private var currentLongitude: Float = 0f
    private var latitudeDestination: Float = 0f
    private var longitudeDestination: Float = 0f

    override fun locationChanged(latitude: String, longitude: String) {
        currentLatitude = latitude.toFloat()
        currentLongitude = longitude.toFloat()
    }

    override fun locationDestinationChanged(latitude: String, longitude: String) {
        latitudeDestination = latitude.toFloat()
        longitudeDestination = longitude.toFloat()
    }

    override fun requestPermission() {
        val permissionFineLocation = ContextCompat.checkSelfPermission(
            getScreen()?.getViewActivity()!!, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionCoarseLocation = ContextCompat.checkSelfPermission(
            getScreen()?.getViewActivity()!!, Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED || permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                getScreen()?.getViewActivity()!!,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
        } else {
            getScreen()?.startLocation()
        }
    }

    override fun onPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    getScreen()?.displayError(
                        getScreen()?.getViewActivity()
                            ?.getString(R.string.permissionsLatitudeNeeded)!!,
                        getScreen()?.getViewActivity()
                            ?.getString(R.string.permissionsLongitudeNeeded)!!
                    )
                } else {
                    getScreen()?.startLocation()
                }
            }
        }
    }

    override fun setLongitude(setLongitude: EditText) {
        this.longitude = setLongitude
//        validateLongitude()
    }

    override fun setLatitude(setLatitude: EditText) {
        this.longitude = setLatitude
//        validateLatitude()
    }

    override fun handleButtonClick() {
        getScreen()!!.runSwipeUp()
    }

    override fun handleSaveButtonClick() {
        getScreen()!!.loadDestination()
    }

    private fun validateLongitude(): Boolean {
        // TODO: 14/12/2020 function has to be change to check correctness of inputed value
        return if (longitude?.text.toString().isEmpty()) {
            getScreen()?.invalidLongitude()
            false
        } else {
            getScreen()?.clearLongitude()
            true
        }
    }

    private fun validateLatitude(): Boolean {
        // TODO: 14/12/2020 function has to be change to check correctness of inputed value
        return if (latitude?.text.toString().isEmpty()) {
            getScreen()?.invalidLatitude()
            false
        } else {
            getScreen()?.clearLatitude()
            true
        }
    }

    // TODO: 16/12/2020
    /** calculates the distance between two locations in km  */
    override fun getDistanceInKm(
        currentLatitude: Float,
        currentLongitude: Float,
        destinationLatitude: Float,
        destinationLongitude: Float
    ): Float {
        val earthRadius =
            6371 // value for km
        val dLat = Math.toRadians((destinationLatitude - currentLatitude).toDouble())
        val dLng = Math.toRadians((destinationLongitude - currentLongitude).toDouble())
        val sindLat = sin(dLat / 2)
        val sindLng = sin(dLng / 2)
        val a =
            Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
                    * Math.cos(Math.toRadians(currentLatitude.toDouble())) * Math.cos(
                Math.toRadians(
                    destinationLatitude.toDouble()
                )
            ))
        val c =
            2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c.toFloat() // output distance, in km
    }

    companion object {
        const val REQUEST_LOCATION_CODE = 1
    }

}