package com.bignerdranch.android.gustieplanner

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import java.text.SimpleDateFormat
import java.util.*

private const val dateFormatString = "MM/dd/yyyy"

class ScheduleActivity: AppCompatActivity(), ScheduleDatePickerFragment.Callbacks {

    private lateinit var addEventButton: ImageButton
    private lateinit var chooseDateButton: Button
    private lateinit var nextDayButton: ImageButton
    private lateinit var prevDayButton: ImageButton
    private lateinit var coursesView: LinearLayout
    private lateinit var todayView: LinearLayout
    private lateinit var upcomingView: LinearLayout

    private val dailyScheduleViewModel : DailyScheduleViewModel by lazy {
        ViewModelProviders.of(this).get(DailyScheduleViewModel::class.java)
    }

    private var allCourses = listOf<Course>()
    private var currentDate = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_schedule)

        addEventButton = findViewById(R.id.schedule_add_event)
        chooseDateButton = findViewById(R.id.schedule_choose_date)
        nextDayButton = findViewById(R.id.schedule_next_day)
        prevDayButton = findViewById(R.id.schedule_previous_day)
        coursesView = findViewById(R.id.daily_schedule_courselist_holder)
        todayView = findViewById(R.id.daily_schedule_today_holder)
        upcomingView = findViewById(R.id.daily_schedule_upcoming_holder)


        addEventButton.setOnClickListener {
            val fragment = EditEventFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.edit_event_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        chooseDateButton.setOnClickListener {
            ScheduleDatePickerFragment.newInstance(currentDate).show(supportFragmentManager, "get_date")
        }

        nextDayButton.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.add(Calendar.DATE, 1)
            currentDate = cal.time
            updateUI(allCourses)
        }

        prevDayButton.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.add(Calendar.DATE, -1)
            currentDate = cal.time
            updateUI(allCourses)
        }

        dailyScheduleViewModel.coursesLiveData.observe(this, androidx.lifecycle.Observer { courses ->
            allCourses = courses
            updateUI(courses)
        })

    }

    override fun onDateSelected(date: Date) {
        currentDate = date
        updateUI(allCourses)
    }

    private fun updateUI(courses: List<Course>) {
        chooseDateButton.text = SimpleDateFormat(dateFormatString).format(currentDate)

        var ct = 1
        coursesView.removeAllViewsInLayout()
        for (course in courses) {
            if (courseIsOnDay(course)) {
                Log.d("Daily Schedule Activity", "Adding course $course to view")
                val textView = TextView(this)
                textView.text = "${ct++}) ${course.name} - ${course.startTimeToString()}"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(R.style.MediumBodyText)
                } else {
                    textView.setTextColor(resources.getColor((R.color.black)))
                    textView.textSize = 16f
                }
                coursesView.addView(textView)
            }
        }
        if (ct == 1) {
            Log.d("Daily Schedule Activity", "No courses to add")
            val textView = TextView(this)
            textView.text = "No courses today"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(R.style.MediumBodyText)
            } else {
                textView.setTextColor(resources.getColor((R.color.black)))
                textView.textSize = 16f
            }
            coursesView.addView(textView)
        }
    }

    private fun courseIsOnDay(course: Course): Boolean {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        val day = cal.get(Calendar.DAY_OF_WEEK)
        return when (day) {
            2 -> course.isOnM
            3 -> course.isOnT
            4 -> course.isOnW
            5 -> course.isOnR
            6 -> course.isOnF
            else -> false
        }
    }
}