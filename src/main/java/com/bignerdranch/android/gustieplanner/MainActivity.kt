package com.bignerdranch.android.gustieplanner

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.lang.Exception
import java.util.*

private const val TAG = "MainActivity"
private const val IS_DARK = "is-dark"
private const val SHOW_MSG = "show-message"

class MainActivity : AppCompatActivity(), HomeFragment.Callbacks, ProfileFragment.Callbacks, DailyScheduleFragment.Callbacks, EditEventFragment.Callbacks {

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var fragmentHolder: FrameLayout
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var doNotShowMsgBtn: Button

    private var showMessage = true

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getSharedPreferences(Keys.sharedPreferencesKey, Context.MODE_PRIVATE)
        val isDark = sharedPreferences.getBoolean(IS_DARK, false)

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.AppTheme2)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.AppTheme1)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = NotificationManagerCompat.from(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    Log.d(TAG, "Starting activity Profile Activity")
                    for (i in 0 until supportFragmentManager.backStackEntryCount) {
                        supportFragmentManager.popBackStack()
                    }
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
                    for (i in 0 until supportFragmentManager.backStackEntryCount) {
                        supportFragmentManager.popBackStack()
                    }
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
                    for (i in 0 until supportFragmentManager.backStackEntryCount) {
                        supportFragmentManager.popBackStack()
                    }
                    val fragment = DailyScheduleFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder, fragment)
                        .addToBackStack(null)
                        .commit()
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_email_contact -> {
                    Log.d(TAG, "Starting email contact")
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        data = Uri.parse("mailto:")
                        type = "text/plain"
                        putExtra(Intent.EXTRA_EMAIL, "fbelik@gustavus.edu")
                        putExtra(Intent.EXTRA_SUBJECT, "Gustie Planner Android App")
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_gustavus_webpage -> {
                    Log.d(TAG, "Starting gustavus webpage")
                    try {
                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                            putExtra(SearchManager.QUERY, "https://www.gustavus.edu/")
                        }
                        startActivity(intent)
                    }
                    catch (e: Exception) {
                        Toast.makeText(this, "Could not access web, error $e", Toast.LENGTH_LONG).show()
                    }
                    true
                }
                R.id.nav_moodle_webpage -> {
                    Log.d(TAG, "Starting moodle webpage")
                    try {
                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                            putExtra(SearchManager.QUERY, "https://moodle.gac.edu/")
                        }
                        startActivity(intent)
                    }
                    catch (e: Exception) {
                        Toast.makeText(this, "Could not access web, error $e", Toast.LENGTH_LONG).show()
                    }
                    true
                }
                R.id.nav_homepage -> {
                    Log.d(TAG, "Starting intro page")
                    val fragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder, fragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        fragmentHolder = findViewById(R.id.fragment_holder)

        showMessage = sharedPreferences.getBoolean(SHOW_MSG, true)

        if (savedInstanceState == null) {
            if (showMessage) {
                val fragment = HomeFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_holder, fragment)
                    .commit()
            }
            else {
                val fragment = CourseScheduleFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_holder, fragment)
                    .commit()
                for (item in navigationView.menu.children) {
                    if (item.itemId == R.id.nav_weekly_schedule) {
                        item.isChecked = true
                    }
                }
            }
        }
    }

    override fun onDoNotShowMsg(dontShow: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(SHOW_MSG, !dontShow)
        editor.apply()
    }

    override fun onSwitchTheme(toDark: Boolean) {
        val editor = sharedPreferences.edit()
        if (toDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putBoolean(IS_DARK, true)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putBoolean(IS_DARK, false)
        }
        editor.apply()
        restartApp()
    }

    override fun onEditCourse(id: UUID, isNew: Boolean) {
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
        else if (showMessage) {
            super.onBackPressed()
            if (supportFragmentManager.backStackEntryCount == 0) {
                for (item in navigationView.menu.children) {
                    item.isChecked = (false)
                }
            }
        }
        else {
            super.onBackPressed()
            if (supportFragmentManager.backStackEntryCount == 0) {
                for (item in navigationView.menu.children) {
                    if (item.itemId == R.id.nav_weekly_schedule) {
                        item.isChecked = true
                    }
                    else {
                        item.isChecked = (false)
                    }
                }
            }
        }
    }

    override fun createNotifications(title: String, description: String, dates: List<Date>, notificationIds: List<Int>) {
        val currentDateTime = Date().time
        for (idx in 0 until 5) {
            if (dates[idx].time != 0L && dates[idx].time > currentDateTime) {
                val activityIntent = Intent(this, NotificationBroadcast::class.java)

                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION_ID, notificationIds[idx])
                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION_TITLE, title)
                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION_DESCRIPTION, description)

                val pendingIntent = PendingIntent.getBroadcast(this, notificationIds[idx], activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                Log.d("MainActivity", "Setting alarm for ${(dates[idx].time - System.currentTimeMillis()) / 1000} secs")

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dates[idx].time, pendingIntent)
                }
                else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, dates[idx].time, pendingIntent)
                }

            }
            else {
                val activityIntent = Intent(this, NotificationBroadcast::class.java)
                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION_ID, notificationIds[idx])
                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION_TITLE, title)
                activityIntent.putExtra(NotificationBroadcast.NOTIFICATION_DESCRIPTION, description)
                val pendingIntent = PendingIntent.getBroadcast(this, notificationIds[idx], activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    private fun restartApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}
