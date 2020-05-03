package com.bignerdranch.android.gustieplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var profileButton: Button
    private lateinit var courseScheduleButton: Button
    private lateinit var scheduleViewButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        this.deleteDatabase("course_database")

        profileButton = findViewById(R.id.profile_button)
        courseScheduleButton = findViewById(R.id.course_schedule_button)
        scheduleViewButton = findViewById(R.id.schedule_view_button)

        profileButton.setOnClickListener {
            Log.d(TAG, "Starting activity Profile Activity")
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        courseScheduleButton.setOnClickListener {
            Log.d(TAG, "Starting activity Course Schedule")
            startActivity(Intent(this, CourseScheduleActivity::class.java))
        }

        scheduleViewButton.setOnClickListener {
            Log.d(TAG, "Starting activity Daily Schedule")
            startActivity(Intent(this, ScheduleActivity::class.java))
        }

    }
}
