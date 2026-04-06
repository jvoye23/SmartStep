package com.jvcodingsolutions.androidapp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.jvcodingsolutions.androidapp.MainActivity
import com.jvcs.androidapp.R
import java.util.Locale

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "step_tracker_channel_v6"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Step Tracker"
        val descriptionText = "Shows your current step progress"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            setShowBadge(true)
            enableLights(true)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotification(steps: Int, goal: Int, calories: Int): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val progress = if (goal > 0) (steps * 100 / (if (goal == 0) 1 else goal)) else 0

        val collapsedView = RemoteViews(context.packageName, R.layout.notification_collapsed).apply {
            setTextViewText(R.id.notification_steps, formattedSteps(steps))
            setTextViewText(R.id.notification_calories, calories.toString())
            setProgressBar(R.id.notification_progress, 100, progress, false)
        }

        val expandedView = RemoteViews(context.packageName, R.layout.notification_expanded).apply {
            setTextViewText(R.id.notification_steps, formattedSteps(steps))
            setTextViewText(R.id.notification_calories, calories.toString())
            setProgressBar(R.id.notification_progress, 100, progress, false)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.steps_tinted)
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setColor(0xFFF9FAFB.toInt())
            .setColorized(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun formattedSteps(steps: Int): String {
        val formattedSteps = steps.toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
        return formattedSteps
    }
}
