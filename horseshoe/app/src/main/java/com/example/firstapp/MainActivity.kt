package com.example.firstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.Sensor
import android.content.Context

import android.widget.Button
import android.view.View

import android.os.Handler

import android.graphics.drawable.AnimationDrawable
import android.graphics.Color

import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private var mSensors: Sensor? = null

    // Used to see magnetic field magnitude for desired object
    // Comment out right before demo
    private lateinit var callibrateButton: Button
    private lateinit var horseImage: ImageView

    // Magnet current value
    private var currMagnitude = 0.0
    // Calibrated magnet value
    private var tarre = 0.0

    // Magnitude of magnetic field for the expected password object (horseshoe)
    private var PASSWORD = 140.0
    // The amount of variance allowed between expected object magnetic field and actual magnitude of magnetic field
    private val threshold = 10.0

    // Indicate that password should be checked
    private var isCalibrated = false



    // ANIMATIONS AND FUN THINGS

    // Stationary horse animation
    private lateinit var horseStationaryAnimation: AnimationDrawable
    private lateinit var horseDancingAnimation: AnimationDrawable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById(R.id.mainlayout) as RelativeLayout
        layout.setBackgroundColor(Color.parseColor("#9C91FF"))

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Define the sensor
        mSensors = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        callibrateButton = findViewById(R.id.calibrateButton) as Button
        horseImage = findViewById<ImageView>(R.id.horseview)

        // Setup horse animation
        horseImage.apply {
            setBackgroundResource(R.drawable.stationaryhorse)
            horseStationaryAnimation = background as AnimationDrawable
        }

        // Set calibration click action
        horseImage.setOnClickListener {
            calibrateMagnet()
        }
    }

    override fun onStart() {
        super.onStart()
        horseStationaryAnimation.start()
    }


    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mSensors, SensorManager.SENSOR_DELAY_NORMAL)


    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {
//        Sensor change value
        if (p0!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD)
        {
            val magX = p0.values[0]
            val magY = p0.values[1]
            val magZ = p0.values[2]

            val magnitude = Math.sqrt((magX * magX + magY * magY + magZ * magZ).toDouble())
            currMagnitude = magnitude
        }

        // REMOVE - show
        callibrateButton.text = (currMagnitude-tarre).toString()

        if(isCalibrated)
        {
            checkPass()
        }
    }


    private fun calibrateMagnet()
    {
        tarre = currMagnitude
        calibratedAnimation()
        isCalibrated = true
    }

    private fun calibratedAnimation() {
        val rainbowView: View = findViewById(R.id.rainbow)

        val rx = rainbowView.width / 2
        val ry = rainbowView.height / 2

        val radius = Math.hypot(rx.toDouble(), ry.toDouble()).toFloat()

        val animator = ViewAnimationUtils.createCircularReveal(rainbowView, rx, ry, 0f, radius)
        animator.setDuration(2000)
        rainbowView.setVisibility(View.VISIBLE)
        animator.start()

    }

    private fun checkPass()
    {
        if(currMagnitude-tarre >= PASSWORD-threshold && currMagnitude-tarre <= PASSWORD+threshold)
        {
            unlock()
        }
    }

    private fun unlock()
    {
        horseview.setVisibility(View.GONE)
        val dancingview = findViewById<ImageView>(R.id.dancingview)
        dancingview.apply {
            setBackgroundResource(R.drawable.dancinghorse)
            horseDancingAnimation = background as AnimationDrawable
        }

        horseDancingAnimation.start()

        val handler = Handler()
        handler.postDelayed(Runnable {
            // Quits the app
            finishAffinity()
        }, 4000)

    }
}