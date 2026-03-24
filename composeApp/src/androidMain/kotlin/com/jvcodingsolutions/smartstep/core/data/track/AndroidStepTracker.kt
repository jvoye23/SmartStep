package com.jvcodingsolutions.smartstep.core.data.track

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import com.jvcodingsolutions.smartstep.features.step_counter.domain.StepTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AndroidStepTracker(
    private val context: Context
) : StepTracker {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    
    private val _stepDeltas = MutableSharedFlow<Int>(extraBufferCapacity = 10)
    override val stepDeltas: Flow<Int> = _stepDeltas.asSharedFlow()

    private var previousTotalSteps: Float = -1f
    private var isTracking = false

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                val currentTotalSteps = event.values[0]
                
                // If it's the first reading since tracking started, or a reboot occurred
                if (previousTotalSteps < 0f || currentTotalSteps < previousTotalSteps) {
                    previousTotalSteps = currentTotalSteps
                    return // No delta on the very first read
                }

                val delta = (currentTotalSteps - previousTotalSteps).toInt()
                if (delta > 0) {
                    _stepDeltas.tryEmit(delta)
                    previousTotalSteps = currentTotalSteps
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun startTracking() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasPermission) {
            // Permission not granted yet; cannot track
            isTracking = false
            return
        }

        if (!isTracking && stepSensor != null) {
            val success = sensorManager.registerListener(
                listener,
                stepSensor,
                SensorManager.SENSOR_DELAY_UI // High enough frequency for responsive UI
            )
            isTracking = success
        }
    }

    override fun stopTracking() {
        if (isTracking) {
            sensorManager.unregisterListener(listener)
            isTracking = false
            previousTotalSteps = -1f // Reset the baseline so we recalibrate on restart
        }
    }
}
