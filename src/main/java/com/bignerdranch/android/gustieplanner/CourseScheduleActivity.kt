package com.bignerdranch.android.gustieplanner

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class CourseScheduleActivity: AppCompatActivity() {

    private val courseScheduleViewModel : CourseScheduleViewModel by lazy {
        ViewModelProviders.of(this).get(CourseScheduleViewModel::class.java)
    }

    private lateinit var title: TextView

    private val dayNames = arrayOf("mon", "tues", "wed", "thurs", "fri")
    private val courseOnDay = Array(5) {false}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_schedule)

        title = findViewById(R.id.course_schedule_header)

        val sharedPreferences = getSharedPreferences(Keys.sharedPreferencesKey, Context.MODE_PRIVATE)
        val usersName = sharedPreferences.getString(Keys.nameKey, "") ?: ""

        if (usersName != "") {
            title.text = usersName + "'s Course Schedule "
        }

        val coursesObserver = Observer<List<Course>> {courses ->
            updateUI(courses)
        }

        courseScheduleViewModel.coursesLiveData.observe(this, coursesObserver)
    }

    private fun updateUI(courses: List<Course>) {
        for (course in courses) {
            courseOnDay[0] = course.isOnM
            courseOnDay[1] = course.isOnT
            courseOnDay[2] = course.isOnW
            courseOnDay[3] = course.isOnR
            courseOnDay[4] = course.isOnF
            var timeString = startTimeConverter(course.startTime)
            if (timeString != "ERROR") {
                for (day in 0 until 5) {
                    if (courseOnDay[day]) {
                        for (time in 1 until roundUp(course.courseTime)) {
                            timeString = startTimeConverter(course.startTime + (time - 1) * 60)
                            if (timeString != "error") {
                                val viewId = resources.getIdentifier(
                                    "${dayNames[day]}_$timeString",
                                    "id",
                                    applicationContext.packageName
                                )
                                val view = findViewById<LinearLayout>(viewId)

                                val text = TextView(this)
                                text.text = course.name
                                text.setTextColor(resources.getColor(R.color.black))
                                text.textSize = 10f

                                val layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1.0f // Layout weight
                                )

                                text.layoutParams = layoutParams
                                text.gravity = Gravity.CENTER
                                text.setBackgroundColor(course.color)

                                view.addView(text)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startTimeConverter(startTime: Int): String {
        return when(startTime) {
            in (8*60 until 9*60) -> "8am"
            in (9*60 until 10*60) -> "9am"
            in (10*60 until 11*60+20) -> "10am"
            in (11*60+20 until 12*60+20) -> "11am"
            in (12*60+20 until 13*60+20) -> "12pm"
            in (13*60+20 until 14*60+20) -> "1pm"
            in (14*60+20 until 15*60+20) -> "2pm"
            in (15*60+20 until 16*60+20) -> "3pm"
            in (16*60+20 until 17*60+20) -> "4pm"
            in (17*60+20 until 18*60+20) -> "5pm"
            in (18*60+20 until 19*60+20) -> "6pm"
            in (19*60+20 until 20*60+20) -> "7pm"
            in (20*60+20 until 21*60+20) -> "8pm"
            in (21*60+20 until 22*60+20) -> "9pm"
            else -> "ERROR"
        }
    }

    private fun roundUp(f: Float): Int {
        return if (f % 1 == 1f) f.toInt() else 1 + f.toInt()
    }

}
