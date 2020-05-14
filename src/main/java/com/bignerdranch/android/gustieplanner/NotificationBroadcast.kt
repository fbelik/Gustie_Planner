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
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_DESCRIPTION = "description"
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == null) {
            val id = intent.getIntExtra(NOTIFICATION_ID, 0)
            val title = intent.getStringExtra(NOTIFICATION_TITLE) ?: "Gustie Planner"
            val description = intent.getStringExtra(NOTIFICATION_DESCRIPTION) ?: "Reminder"
            val notification = NotificationCompat.Builder(context, GustiePlannerApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .build()

            Log.d("NotificationBroadcast", "Sending notification with id $id - $notification")

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(id, notification)
        }
        else {
            Log.d("NotificationBroadcast","intent.action=${intent.action}")
        }

    }


}