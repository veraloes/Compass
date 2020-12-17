package com.michalska.compass.feature.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ORIENTATION
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.michalska.compass.R
import com.michalska.compass.utils.LocationService
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),
    MainContract.View, SensorEventListener,
    LocationService.LocationListener {

    private var presenter = MainPresenter()
    private var sensorManager: SensorManager? = null
    private var currentDegree = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attach(this)

        getSystemService(Context.SENSOR_SERVICE) as SensorManager

        presenter.requestPermission()
        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        longitude_edit_text.addTextChangedListener {
            presenter.setLongitude(
                setLongitude = EditText(
                    this
                )
            )
        }
        latitude_edit_text.addTextChangedListener {
            presenter.setLatitude(
                setLatitude = EditText(
                    this
                )
            )
        }

        set_coordiantes_button.setOnClickListener {
            presenter.handleButtonClick()
        }
    }

    override fun displayError(latitudeError: String, longitudeError: String) {
        this.latitude_text.text = latitudeError
        this.longitude_text.text = longitudeError
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun invalidLongitude() {
        longitude_edit_text.error = getString(R.string.longitude_error)
    }

    override fun invalidLatitude() {
        latitude_edit_text.error = getString(R.string.latitude_error)
    }

    override fun clearLongitude() {
        longitude_edit_text.error = null
    }

    override fun clearLatitude() {
        latitude_edit_text.error = null
    }

    private fun getLatitudeInput(): String {
        return latitude_edit_text.text.toString()
            .substring(0, Math.min(latitude_edit_text.length(), 5))
    }

    private fun getLongitudeInput(): String {
        return longitude_edit_text.text.toString()
            .substring(0, Math.min(longitude_edit_text.length(), 5))
    }

    private fun getCurrentLongitude(): String {
        return longitude_text.text.toString().substring(0, Math.min(longitude_text.length(), 5))
    }

    private fun getCurrentLatitude(): String {
        return latitude_text.text.toString().substring(0, Math.min(latitude_text.length(), 5))
    }

    override fun runSwipeUp() {
        set_coordinates_layout.isVisible = true
        val animationUp = AnimationUtils.loadAnimation(
            this, R.anim.slide_up
        )
        set_coordinates_layout.startAnimation(animationUp)
        set_coordiantes_button.startAnimation(animationUp)

        set_coordiantes_button.isClickable = false
        imageView.isVisible = true

        save_coordinates_button.setOnClickListener {
            presenter.handleSaveButtonClick()
        }
    }

    override fun loadDestination() {
        longitude_edit_text.isVisible = false
        latitude_edit_text.isVisible = false
        save_coordinates_button.isVisible = false
        set_coordinates_layout.isVisible = true
        progressBar.isVisible = true
        // TODO: 14/12/2020
        //  Need check correctness of inputed longitude and latitude values.
        destination_coordinates.isVisible = true
        this.displayDestinationLocation(
            latitude = getLatitudeInput(),
            longitude = getLongitudeInput()
        )
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val degree: Int = (event?.values?.get(0)!!).roundToInt()
        val rotateCompassAnimation = RotateAnimation(
            currentDegree,
            (-degree).toFloat(),
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotateCompassAnimation.duration = 200
        rotateCompassAnimation.fillAfter = true

        compass_image.startAnimation(rotateCompassAnimation)
        currentDegree = (-degree).toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onGpsLocationChanged(latitude: String, longitude: String) {
        this.latitude_text.text = "$latitude N".trim()
        this.longitude_text.text = "$longitude E".trim()
        presenter.locationChanged(latitude, longitude)
    }

    @SuppressLint("SetTextI18n")
    override fun setDistance(): Float {
        val distanceValue = presenter.getDistanceInKm(
            currentLatitude = getCurrentLatitude().toFloat(),
            currentLongitude = getCurrentLongitude().toFloat(),
            destinationLatitude = getLatitudeInput().toFloat(),
            destinationLongitude = getLongitudeInput().toFloat()
        )

        distance.text = "$distanceValue m"
        distance.isVisible = true
        distance_info.isVisible = true
        return distanceValue
    }

    override fun showUpdatedLocation(latitude: String, longitude: String) {
    }

    @SuppressLint("SetTextI18n")
    override fun displayDestinationLocation(latitude: String, longitude: String) {
        this.latitude_destination.text = "$latitude N"
        this.longitude_destination.text = "$longitude E"
        presenter.locationDestinationChanged(latitude, longitude)
    }

    @SuppressLint("MissingPermission")
    override fun startLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        LocationService(this, locationManager)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        progressBar.isVisible = false
        set_coordinates_layout.isVisible = false
        imageView.isVisible = false
        set_coordiantes_button.isVisible = false
        setDistance()
    }

}