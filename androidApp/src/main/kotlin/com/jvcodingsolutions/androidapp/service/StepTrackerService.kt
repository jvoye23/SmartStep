package com.jvcodingsolutions.androidapp.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.jvcodingsolutions.androidapp.notification.NotificationHelper
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.presentation.util.calculateCalories
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.ext.android.inject

class StepTrackerService : Service() {

    private val trackRepository: TrackRepository by inject()
    private val profileStorage: ProfileStorage by inject()
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var notificationHelper: NotificationHelper
    
    override fun onCreate() {
        super.onCreate()
        Log.d("StepTrackerService", "Service Created")
        notificationHelper = NotificationHelper(this)
    }

    private var numberOfStepsNotWrittenToDb = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("StepTrackerService", "onStartCommand")
        val steps = intent?.getIntExtra("steps", 0) ?: 0
        val goal = intent?.getIntExtra("goal", 0) ?: 0
        val calories = intent?.getIntExtra("calories", 0) ?: 0
        
        numberOfStepsNotWrittenToDb = 0 // Reset when starting or re-starting with new intent data

        val notification = notificationHelper.buildNotification(steps, goal, calories)
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NotificationHelper.NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                )
            } else {
                startForeground(
                    NotificationHelper.NOTIFICATION_ID,
                    notification
                )
            }
            Log.d("StepTrackerService", "startForeground called successfully")
        } catch (e: Exception) {
            Log.e("StepTrackerService", "Failed to start foreground service", e)
        }

        observeSteps()
        
        return START_STICKY
    }

    private fun observeSteps() {
        serviceScope.launch {
            try {
                val profile = profileStorage.get()
                if (profile != null) {
                    val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    Log.d("StepTrackerService", "Observing steps for $today")

                    val stepTracker: com.jvcodingsolutions.smartstep.features.step_counter.domain.StepTracker by inject()
                    
                    launch {
                        stepTracker.stepDeltas.collect { delta ->
                            numberOfStepsNotWrittenToDb += delta
                        }
                    }
                    
                    combine(
                        trackRepository.getCurrentStepsFlow(profile.id, today),
                        trackRepository.getCurrentStepGoalFlow(profile.id, today)
                    ) { steps, goal ->
                        val currentSteps = (steps ?: 0) + numberOfStepsNotWrittenToDb
                        val currentGoal = goal ?: 6000
                        val calories = calculateCalories(currentSteps, profile)
                        Triple(currentSteps, currentGoal, calories)
                    }.collect { (steps, goal, calories) ->
                        Log.d("StepTrackerService", "Steps updated (DB + Local): $steps / $goal | Calories: $calories")
                        val notification = notificationHelper.buildNotification(steps, goal, calories)
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
                        notificationManager.notify(NotificationHelper.NOTIFICATION_ID, notification)
                    }
                } else {
                    Log.w("StepTrackerService", "No profile found, cannot observe steps")
                }
            } catch (e: Exception) {
                Log.e("StepTrackerService", "Error in observeSteps", e)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d("StepTrackerService", "Service Destroyed")
        super.onDestroy()
        serviceScope.cancel()
    }
}
