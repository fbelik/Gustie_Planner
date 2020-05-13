package com.bignerdranch.android.gustieplanner

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationBroadcast: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "the-id"
        const val NOTIFICATION = "notification"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)

        Log.d("NotificationBroadcast", "Sending notification with id $id - $notification")

        val notificationManager = NotificationManagerCompat.from(context)

        notification?.let{
            notificationManager.notify(id, it)
        }

    }


}