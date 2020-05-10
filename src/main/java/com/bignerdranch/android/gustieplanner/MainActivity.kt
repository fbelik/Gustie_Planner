package com.bignerdranch.android.gustieplanner

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), ProfileFragment.Callbacks, DailyScheduleFragment.Callbacks, EditEventFragment.Callbacks {

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var fragmentHolder: FrameLayout
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = NotificationManagerCompat.from(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    Log.d(TAG, "Starting activity Profile Activity")
                    val fragment = ProfileFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder, fragment)
                        .addToBackStack(null)
                        .commit()
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_weekly_schedule -> {
                    Log.d(TAG, "Starting activity Course Schedule")
                    val fragment = CourseScheduleFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder, fragment)
                        .addToBackStack(null)
                        .commit()
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_daily_schedule -> {
                    Log.d(TAG, "Starting activity Daily Schedule")
                    val fragment = DailyScheduleFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder, fragment)
                        .addToBackStack(null)
                        .commit()
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        fragmentHolder = findViewById(R.id.fragment_holder)

        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .commit()
        }
    }

    override fun onEditEventFragment(id: UUID, isNew: Boolean) {
        val fragment = EditCourseFragment.newInstance(id, isNew)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_holder, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onEditEvent(id: UUID, isNew: Boolean) {
        val fragment = EditEventFragment.newInstance(id, isNew)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_holder, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    override fun createNotifications(title: String, description: String, dates: List<Date>, notificationIds: List<Int>) {
        val currentDateTime = Date().time
        for (idx in 0 until 5) {
            if (dates[idx].time != 0L) {
                val activityIntent = Intent(this, NotificationBroadcast::class.java)

                val notification = NotificationCompat.Builder(this, GustiePlannerApplication.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setColor(Color.BLUE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setAutoCancel(true)
                    .build()

                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION_ID, notificationIds[idx])
                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION, notification)

                val pendingIntent = PendingIntent.getBroadcast(this, notificationIds[idx], activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val notificationTime = SystemClock.elapsedRealtime() + (dates[idx].time - currentDateTime)

                Log.d(TAG, "Setting alarm with id ${notificationIds[idx]} for ${(notificationTime-SystemClock.elapsedRealtime())/1000} sec in future")

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, notificationTime, pendingIntent)

            }
            else {
                notificationManager.cancel(notificationIds[idx])
            }
        }
    }
}
