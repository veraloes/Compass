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
import android.os.Handler
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.michalska.compass.R
import com.michalska.compass.databinding.ActivityMainBinding
import com.michalska.compass.utils.LocationService
import kotlin.math.min
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),
    MainContract.View, SensorEventListener,
    LocationService.LocationListener {

    private lateinit var binding: ActivityMainBinding
    private var presenter = MainPresenter()
    private var sensorManager: SensorManager? = null
    private var currentDegree = 0f
    private val progressBarHandler = Handler()
    private val destinationDirection = 0f
    private val lastDestinationDirection = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.attach(this)

        getSystemService(Context.SENSOR_SERVICE) as SensorManager

        presenter.requestPermission()
        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?

        initListeners()
    }

    private fun initListeners() {
        binding.longitudeEditText.addTextChangedListener {
            presenter.setLongitude(
                longitude = EditText(
                    this
                )
            )
        }
        binding.latitudeEditText.addTextChangedListener {
            presenter.setLatitude(
                latitude = EditText(
                    this
                )
            )
        }

        binding.setCoordiantesButton.setOnClickListener {
            presenter.handleButtonClick()
        }
    }

    override fun displayError(latitudeError: String, longitudeError: String) {
        this.binding.latitudeText.text = latitudeError
        this.binding.longitudeText.text = longitudeError
    }

    override fun getViewActivity(): Activity {
        return this
    }

    private fun getCurrentLongitude(): String {
        return binding.longitudeText.text.toString()
            .substring(0, min(binding.longitudeText.length(), 5))
    }

    private fun getCurrentLatitude(): String {
        return binding.latitudeText.text.toString()
            .substring(0, min(binding.latitudeText.length(), 5))
    }

    private fun getLatitudeInput(): String {
        return binding.latitudeEditText.text.toString()
            .substring(0, min(binding.latitudeEditText.length(), 5))
    }

    private fun getLongitudeInput(): String {
        return binding.longitudeEditText.text.toString()
            .substring(0, min(binding.longitudeEditText.length(), 5))
    }

    override fun invalidLongitude() {
        binding.longitudeEditText.error = getString(R.string.longitude_error)
    }

    override fun invalidLatitude() {
        binding.latitudeEditText.error = getString(R.string.latitude_error)
    }

    override fun clearLongitude() {
        binding.longitudeEditText.error = null
    }

    override fun clearLatitude() {
        binding.latitudeEditText.error = null
    }

    override fun runSwipeUp() {
        binding.setCoordinatesLayout.isVisible = true
        val animationUp = AnimationUtils.loadAnimation(
            this, R.anim.slide_up
        )
        binding.setCoordinatesLayout.startAnimation(animationUp)
        binding.setCoordiantesButton.startAnimation(animationUp)

        binding.setCoordiantesButton.isClickable = false
        binding.imageView.isVisible = true

        binding.saveCoordinatesButton.setOnClickListener {
            checkInput()
        }
    }

    private fun invalidBothToast() {
        val toast =
            Toast.makeText(
                applicationContext,
                getString(R.string.blank_coordintes),
                Toast.LENGTH_SHORT
            )
        toast.setGravity(Gravity.TOP, X_OFFSET, Y_OFFSET)
        toast.show()
    }

    private fun invalidLatitudeToast() {
        val toast: Toast =
            Toast.makeText(
                applicationContext,
                getString(R.string.blank_latitude),
                Toast.LENGTH_SHORT
            )
        toast.setGravity(Gravity.TOP, X_OFFSET, Y_OFFSET)
        toast.show()
    }

    private fun invalidLongitudeToast() {
        val toast: Toast =
            Toast.makeText(
                applicationContext,
                getString(R.string.blank_longitude),
                Toast.LENGTH_SHORT
            )
        toast.setGravity(Gravity.TOP, X_OFFSET, Y_OFFSET)
        toast.show()
    }

    private fun checkInput() {
        val inputLatitude = getLatitudeInput()
        val inputLongitude = getLongitudeInput()
        when {
            inputLatitude.isEmpty() && inputLongitude.isEmpty() -> {
                invalidBothToast()

            }
            inputLatitude.isEmpty() || inputLatitude == "." -> {
                invalidLatitudeToast()

            }
            inputLongitude.isEmpty() || inputLongitude == "." -> {
                invalidLongitudeToast()

            }
            else -> {
                presenter.handleSaveButtonClick()

            }
        }
    }

    override fun loadDestination() {
        binding.longitudeEditText.isVisible = false
        binding.latitudeEditText.isVisible = false
        binding.saveCoordinatesButton.isVisible = false
        binding.setCoordinatesLayout.isVisible = true
        binding.progressBar.isVisible = true
        binding.destinationCoordinates.isVisible = true
        this.displayDestinationLocation(
            latitude = getLatitudeInput(),
            longitude = getLongitudeInput()
        )
        progressBarHandler.postDelayed({
            hideWindowShowInfo()
        }, DELAY)
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
            PIVOT_VALUE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_VALUE
        )

        rotateCompassAnimation.duration = DURATION
        rotateCompassAnimation.fillAfter = true

        binding.compassImage.startAnimation(rotateCompassAnimation)
        currentDegree = (-degree).toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onGpsLocationChanged(latitude: String, longitude: String) {
        this.binding.latitudeText.text =
            latitude.substring(0, min(binding.latitudeText.length(), 9)) + getString(R.string.n)
        this.binding.longitudeText.text =
            longitude.substring(0, min(binding.longitudeText.length(), 9)) + getString(R.string.e)
        presenter.locationChanged(latitude, longitude)
    }

    @SuppressLint("SetTextI18n")
    override fun setDistance(): Int {
        val distanceValue = presenter.getDistanceInKm(
            currentLatitude = getCurrentLatitude().toDouble(),
            currentLongitude = getCurrentLongitude().toDouble(),
            latitudeDestination = getLatitudeInput().toDouble(),
            longitudeDestination = getLongitudeInput().toDouble()
        )

        binding.distance.text = distanceValue.toString() + getString(R.string.kilometers)
        binding.distance.isVisible = true
        binding.distanceInfo.isVisible = true
        return distanceValue
    }

    override fun showUpdatedLocation(latitude: String, longitude: String) {
    }

    @SuppressLint("SetTextI18n")
    override fun displayDestinationLocation(latitude: String, longitude: String) {
        this.binding.latitudeDestination.text = latitude + getString(R.string.n)
        this.binding.longitudeDestination.text = longitude + getString(R.string.e)
        presenter.locationDestinationChanged(latitude, longitude)
    }

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

    private fun hideWindowShowInfo() {
        binding.progressBar.isVisible = false
        binding.setCoordinatesLayout.isVisible = false
        binding.imageView.isVisible = false
        binding.setCoordiantesButton.isVisible = false
        setDistance()
    }

    override fun onBackPressed() {}

    companion object {
        private const val DELAY: Long = 500
        private const val PIVOT_VALUE: Float = 0.5F
        private const val X_OFFSET: Int = 0
        private const val Y_OFFSET: Int = 50
        private const val DURATION: Long = 200

    }
}