package com.bignerdranch.android.gustieplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), ProfileFragment.Callbacks, DailyScheduleFragment.Callbacks {

    private lateinit var fragmentHolder: FrameLayout
    private lateinit var profileButton: Button
    private lateinit var courseScheduleButton: Button
    private lateinit var scheduleViewButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileButton = findViewById(R.id.profile_button)
        courseScheduleButton = findViewById(R.id.course_schedule_button)
        scheduleViewButton = findViewById(R.id.schedule_view_button)
        fragmentHolder = findViewById(R.id.fragment_holder)

        profileButton.setOnClickListener {
            Log.d(TAG, "Starting activity Profile Activity")
            val fragment = ProfileFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit()
        }

        courseScheduleButton.setOnClickListener {
            Log.d(TAG, "Starting activity Course Schedule")
            val fragment = CourseScheduleFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
                .commit()
        }

        scheduleViewButton.setOnClickListener {
            Log.d(TAG, "Starting activity Daily Schedule")
            val fragment = DailyScheduleFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(null)
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
        //Todo
    }
}
