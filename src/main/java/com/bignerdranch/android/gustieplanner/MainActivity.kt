package com.bignerdranch.android.gustieplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), ProfileFragment.Callbacks, DailyScheduleFragment.Callbacks {

    private lateinit var fragmentHolder: FrameLayout
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
