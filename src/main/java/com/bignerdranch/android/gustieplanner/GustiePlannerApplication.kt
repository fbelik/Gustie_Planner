package com.bignerdranch.android.gustieplanner

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class GustiePlannerApplication: Application() {

    companion object {
        val NOTIFICATION_CHANNEL_ID = "notification_channel"
    }

    override fun onCreate() {
        super.onCreate()
        CourseRepository.initialize(this)
        EventRepository.initialize(this)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Event Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Used to provide notifications for events created by the user"
            val manager = getSystemService(NotificationManager::class.java)
            if (manager == null) {
                // Error, cannot create notifications
            }
            else {
                manager.createNotificationChannel(channel)
            }
        }
    }

}