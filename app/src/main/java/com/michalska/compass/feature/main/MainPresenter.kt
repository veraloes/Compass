package com.michalska.compass.feature.main

import android.Manifest
import android.content.pm.PackageManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.michalska.compass.R
import com.michalska.compass.base.BasePresenter


class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {

    private var longitude: EditText? = null
    private var latitude: EditText? = null
    private var currentLatitude: Float = 0f
    private var currentLongitude: Float = 0f

    override fun locationChanged(latitude: String, longitude: String) {
        currentLatitude = latitude.toFloat()
        currentLongitude = longitude.toFloat()
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
                    getScreen()?.showLocationError(
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

    override fun setLongitude(text1: EditText) {
        this.longitude = text1
        validateLongitude()
    }

    override fun setLatitude(text2: EditText) {
        this.longitude = text2
        validateLatitude()
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

    companion object {
        const val REQUEST_LOCATION_CODE = 1
    }

}