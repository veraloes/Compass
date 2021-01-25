package com.michalska.compass.feature.main

import android.Manifest
import android.content.pm.PackageManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.michalska.compass.R
import com.michalska.compass.base.BasePresenter
import kotlin.math.*


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

    override fun setLongitude(longitude: EditText) {
        this.longitude = longitude
        validateLongitude()
    }

    override fun setLatitude(latitude: EditText) {
        this.latitude = latitude
        validateLatitude()
    }

    override fun handleButtonClick() {
        getScreen()!!.runSwipeUp()
    }

    override fun handleSaveButtonClick() {
        getScreen()!!.loadDestination()
    }

    private fun validateLongitude(): Boolean {
        return if (longitudeDestination.toString() == "") {
            getScreen()?.invalidLongitude()
            false
        } else {
            getScreen()?.clearLongitude()
            true
        }
    }

    private fun validateLatitude(): Boolean {
        return if (latitudeDestination.toString() == "") {
            getScreen()?.invalidLatitude()
            false
        } else {
            getScreen()?.clearLatitude()
            true
        }
    }

    override fun getDistanceInKm(
        currentLatitude: Double, latitudeDestination: Double, currentLongitude: Double,
        longitudeDestination: Double
    ): Int {
        val radius = 6371 // Radius of the earth
        val latDistance = Math.toRadians(latitudeDestination - currentLatitude)
        val lonDistance = Math.toRadians(longitudeDestination - currentLongitude)
        val a =
            (sin(latDistance / 2) * sin(latDistance / 2)
                    + (cos(Math.toRadians(currentLatitude)) * cos(
                Math.toRadians(
                    longitudeDestination
                )
            )
                    * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c =
            2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = radius * c  // convert to km, if want m *1000
        distance = distance.pow(2.0)
        return sqrt(distance).roundToInt()
    }

    companion object {
        const val REQUEST_LOCATION_CODE: Int = 1
    }

}