package com.bignerdranch.android.gustieplanner

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.text.SimpleDateFormat
import java.util.*

private const val dateFormatString = "MM/dd/yyyy"
private const val dateTimeFormatString = "MM/dd h:mm a"
private const val timeFormatString = "h:mm a"
private const val REQUEST_DATE = 0
private const val MAX_UPCOMING_EVENTS = 6
private const val DELETE_OLD_EVENTS: Long = 7884000000L

class DailyScheduleFragment: Fragment(), ScheduleDatePickerFragment.Callbacks {

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

    interface Callbacks {
        fun onEditEvent(id: UUID, isNew: Boolean)
    }

    private var callbacks: Callbacks? = null

    private var allEvents = listOf<Event>()
    private var allCourses = listOf<Course>()
    private var currentDate = Date()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_schedule, container, false)

        addEventButton = view.findViewById(R.id.schedule_add_event)
        chooseDateButton = view.findViewById(R.id.schedule_choose_date)
        nextDayButton = view.findViewById(R.id.schedule_next_day)
        prevDayButton = view.findViewById(R.id.schedule_previous_day)
        coursesView = view.findViewById(R.id.daily_schedule_courselist_holder)
        todayView = view.findViewById(R.id.daily_schedule_today_holder)
        upcomingView = view.findViewById(R.id.daily_schedule_upcoming_holder)

        return view
    }

    override fun onStart() {
        super.onStart()

        addEventButton.setOnClickListener {
            val event = Event(time = Date())
            dailyScheduleViewModel.addEvent(event)
            callbacks?.onEditEvent(event.id, isNew=true)
        }

        chooseDateButton.setOnClickListener {
            activity?.let {
                ScheduleDatePickerFragment.newInstance(currentDate).apply {
                    setTargetFragment(this@DailyScheduleFragment, REQUEST_DATE)
                    show(it.supportFragmentManager, "get_date")
                }
            }
        }

        nextDayButton.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.add(Calendar.DATE, 1)
            currentDate = cal.time
            updateUI(allCourses, allEvents)
        }

        prevDayButton.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.time = currentDate
            cal.add(Calendar.DATE, -1)
            currentDate = cal.time
            updateUI(allCourses, allEvents)
        }

        dailyScheduleViewModel.eventsLiveData.observe(this, androidx.lifecycle.Observer { events ->
            allEvents = events
            Log.d("DailyScheduleFragment", "Received ${allEvents.size} events")
            updateUI(allCourses, allEvents)
        })

        dailyScheduleViewModel.coursesLiveData.observe(this, androidx.lifecycle.Observer { courses ->
            allCourses = courses
            updateUI(allCourses, allEvents)
        })

    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDateSelected(date: Date) {
        currentDate = date
        updateUI(allCourses, allEvents)
    }

    private fun updateUI(courses: List<Course>, events: List<Event>) {
        chooseDateButton.text = SimpleDateFormat(dateFormatString).format(currentDate)

        var todayCt = 1
        var upcomingCt = 1
        todayView.removeAllViewsInLayout()
        upcomingView.removeAllViewsInLayout()
        for (event in events) {
            val eventDayCode = dayCode(event.time)
            if (eventDayCode == 0) {
                todayCt++
                val newView = LayoutInflater.from(activity).inflate(R.layout.event_view, null)
                val layout = newView.findViewById<LinearLayout>(R.id.event_list_layout)
                val typeTxt = newView.findViewById<TextView>(R.id.event_list_type)
                val titleTxt = newView.findViewById<TextView>(R.id.event_list_title)
                val deleteBtn = newView.findViewById<ImageButton>(R.id.event_list_delete)

                for (course in allCourses) {
                    if (course.id == event.courseId) {
                        layout.background.setTint(course.color)
                        break
                    }
                }
                layout.setOnClickListener {
                    callbacks?.onEditEvent(event.id, isNew = false)
                }
                typeTxt.text = event.type
                titleTxt.text = "${event.name} - ${SimpleDateFormat(timeFormatString).format(event.time)}"
                deleteBtn.setOnClickListener {
                    dailyScheduleViewModel.deleteEvent(event.id)
                    updateUI(allCourses, allEvents)
                }
                newView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0,0,0,10)
                }
                todayView.addView(newView)
            }
            else if (eventDayCode == 1 && upcomingCt <= MAX_UPCOMING_EVENTS) {
                upcomingCt++
                val newView = LayoutInflater.from(activity).inflate(R.layout.event_view, null)
                val layout = newView.findViewById<LinearLayout>(R.id.event_list_layout)
                val typeTxt = newView.findViewById<TextView>(R.id.event_list_type)
                val titleTxt = newView.findViewById<TextView>(R.id.event_list_title)
                val deleteBtn = newView.findViewById<ImageButton>(R.id.event_list_delete)

                for (course in allCourses) {
                    if (course.id == event.courseId) {
                        layout.background.setTint(course.color)
                        break
                    }
                }
                layout.setOnClickListener {
                    callbacks?.onEditEvent(event.id, isNew = false)
                }
                typeTxt.text = event.type
                titleTxt.text = "${event.name} - ${SimpleDateFormat(dateTimeFormatString).format(event.time)}"
                deleteBtn.setOnClickListener {
                    dailyScheduleViewModel.deleteEvent(event.id)
                    updateUI(allCourses, allEvents)
                }
                newView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0,0,0,10)
                }
                upcomingView.addView(newView)
            }
            else {
                // Event is old, can remove
                if (Date().time - event.time.time > DELETE_OLD_EVENTS)
                dailyScheduleViewModel.deleteEvent(event.id)
            }
        }

        var ct = 1
        coursesView.removeAllViewsInLayout()
        for (course in courses) {
            if (courseIsOnDay(course)) {
                Log.d("Daily Schedule Activity", "Adding course $course to view")
                val textView = TextView(activity)
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
            val textView = TextView(activity)
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

    private fun dayCode(date1: Date): Int {
        val cal1 = Calendar.getInstance()
        cal1.time = date1

        val cal2 = Calendar.getInstance()
        cal2.time = currentDate

        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
            return 0
        }
        else if (cal2.time < cal1.time) {
            return 1
        }
        else {
            return 2
        }
    }

    private fun courseIsOnDay(course: Course): Boolean {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            2 -> course.isOnM
            3 -> course.isOnT
            4 -> course.isOnW
            5 -> course.isOnR
            6 -> course.isOnF
            else -> false
        }
    }
}