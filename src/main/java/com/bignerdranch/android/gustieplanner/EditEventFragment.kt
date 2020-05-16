package com.bignerdranch.android.gustieplanner

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.text.SimpleDateFormat
import java.util.*

private const val EVENT_ID_KEY = "event_id"
private const val NEW_EVENT_KEY = "new_event"
private const val date_format_string = "E LLL dd yy"
private const val time_format_string = "h:mm a"
private const val REQUEST_DATE = 1
private const val REQUEST_TIME = 2
private const val MAX_TITLE_LEN = 25
private const val MAX_DESCRIPTION_LEN = 110

class EditEventFragment: Fragment(), EditEventDatePickerFragment.Callbacks, EditEventTimePickerFragment.Callbacks, EventNotificationDatePickerFragment.Callbacks, EventNotificationTimePickerFragment.Callbacks {

    private lateinit var eventName: EditText
    private lateinit var courseSpinner: Spinner
    private lateinit var eventDescription: EditText
    private lateinit var editDateButton: Button
    private lateinit var editTimeButton: Button
    private lateinit var eventTypeRadioGroup: RadioGroup
    private lateinit var newNotificationButton: ImageButton
    private lateinit var notificationContainer: LinearLayout
    private lateinit var noNotificationsText: TextView
    private lateinit var submitBtn: Button

    private var isNew = false

    interface Callbacks {
        fun createNotifications(title: String, description: String, dates: List<Date>, notificationIds: List<Int>)
    }

    private var callbacks: Callbacks? = null

    private val editEventViewModel: EditEventViewModel by lazy {
        ViewModelProviders.of(this).get(EditEventViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_event, container, false)

        eventName = view.findViewById(R.id.edit_event_name)
        courseSpinner = view.findViewById(R.id.edit_event_course_spinner)
        eventDescription = view.findViewById(R.id.edit_event_description)
        editDateButton = view.findViewById(R.id.edit_event_date_btn)
        editTimeButton = view.findViewById(R.id.edit_event_time_btn)
        eventTypeRadioGroup = view.findViewById(R.id.edit_event_type_radio_group)
        newNotificationButton = view.findViewById(R.id.edit_event_new_notification)
        notificationContainer = view.findViewById(R.id.edit_event_notification_list_container)
        noNotificationsText = view.findViewById(R.id.edit_event_no_notifications_text)
        submitBtn = view.findViewById(R.id.edit_event_submit)

        editEventViewModel.editEventUUID = arguments?.getSerializable(EVENT_ID_KEY) as UUID

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onStart() {
        super.onStart()

        eventName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length > MAX_TITLE_LEN) {
                    eventName.setText(text.slice(0 until MAX_TITLE_LEN))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        eventDescription.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length > MAX_DESCRIPTION_LEN) {
                    eventDescription.setText(text.slice(0 until MAX_DESCRIPTION_LEN))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editDateButton.setOnClickListener {
            activity?.let {
                EditEventDatePickerFragment.newInstance(editEventViewModel.eventDate).apply {
                    setTargetFragment(this@EditEventFragment, REQUEST_DATE)
                    show(it.supportFragmentManager, "get_date")
                }
            }
        }

        editTimeButton.setOnClickListener {
            activity?.let {
                EditEventTimePickerFragment.newInstance(editEventViewModel.eventDate).apply {
                    setTargetFragment(this@EditEventFragment, REQUEST_TIME)
                    show(it.supportFragmentManager, "get_time")
                }
            }
        }

        newNotificationButton.setOnClickListener {
            val newIdx = editEventViewModel.eventNotifications.size
            if (newIdx < 5) {
                editEventViewModel.eventNotifications.add(editEventViewModel.eventDate)
                updateUINotifications()
            }
        }

        submitBtn.setOnClickListener {
            onSubmit()
        }

        eventTypeRadioGroup.check(R.id.edit_event_assignment_radio)

        isNew = arguments?.getSerializable(NEW_EVENT_KEY) as Boolean
        val eventId = arguments?.getSerializable(EVENT_ID_KEY) as UUID

        editEventViewModel.coursesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { courses ->
            editEventViewModel.allCourses = courses
            updateUICourseList()
        })
        editEventViewModel.loadEvent(eventId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editEventViewModel.eventLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { incomingEvent ->
                Log.d("EditEventFragment", "Incoming event ${incomingEvent}")
                incomingEvent?.let {
                    Log.d("EditEventFragment", "Received event, setting values")
                    editEventViewModel.event = it
                    editEventViewModel.eventDate = it.time
                    eventName.setText(it.name)
                    eventDescription.setText(it.description)
                    editDateButton.text = SimpleDateFormat(date_format_string).format(it.time)
                    editTimeButton.text = SimpleDateFormat(time_format_string).format(it.time)
                    eventTypeRadioGroup.check(
                        when (it.type) {
                            "Exam" -> R.id.edit_event_exam_radio
                            "Event" -> R.id.edit_event_event_radio
                            else -> R.id.edit_event_assignment_radio
                        }
                    )
                    eventTypeRadioGroup.jumpDrawablesToCurrentState()
                    courseSpinner.adapter?.apply {
                        var spinnerIdx = 0
                        for (cIdx in editEventViewModel.allCourses.indices) {
                            if (it.courseId == editEventViewModel.allCourses[cIdx].id) {
                                spinnerIdx = cIdx + 1
                                break
                            }
                        }
                        courseSpinner.setSelection(spinnerIdx)
                    }
                    val currentTime = Date().time
                    if (it.notification1_time.time != 0L && it.notification1_time.time < currentTime) {
                        it.notification1_time.time = 0L
                    }
                    if (it.notification2_time.time != 0L && it.notification2_time.time < currentTime) {
                        it.notification2_time.time = 0L
                    }
                    if (it.notification3_time.time != 0L && it.notification3_time.time < currentTime) {
                        it.notification3_time.time = 0L
                    }
                    if (it.notification4_time.time != 0L && it.notification4_time.time < currentTime) {
                        it.notification4_time.time = 0L
                    }
                    if (it.notification5_time.time != 0L && it.notification5_time.time < currentTime) {
                        it.notification5_time.time = 0L
                    }
                    for (i in 0 until 5) {
                        if (it.notification1_time.time != 0L) {
                            break
                        }
                        it.notification1_time.time = it.notification2_time.time
                        it.notification2_time.time = it.notification3_time.time
                        it.notification3_time.time = it.notification4_time.time
                        it.notification4_time.time = it.notification5_time.time
                        it.notification5_time.time = 0L
                    }
                    courseSpinner.jumpDrawablesToCurrentState()
                    editEventViewModel.eventNotifications.clear()
                    if (it.notification1_time.time != 0L) {
                        editEventViewModel.eventNotifications.add(it.notification1_time)
                        if (it.notification2_time.time != 0L) {
                            editEventViewModel.eventNotifications.add(it.notification2_time)
                            if (it.notification3_time.time != 0L) {
                                editEventViewModel.eventNotifications.add(it.notification3_time)
                                if (it.notification4_time.time != 0L) {
                                    editEventViewModel.eventNotifications.add(it.notification4_time)
                                    if (it.notification5_time.time != 0L) {
                                        editEventViewModel.eventNotifications.add(it.notification5_time)
                                    }
                                }
                            }
                        }
                    }
                    updateUINotifications()
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        if (!fieldsFilled() && isNew) {
            editEventViewModel.deleteEvent(editEventViewModel.editEventUUID)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun onSubmit() {
        if (!fieldsFilled()) {
            AlertDialog.Builder(context).apply {
                setTitle("Error")
                setMessage("One of the fields was not filled")
                setCancelable(false)
                setPositiveButton("Ok") { dialog, id ->
                }
                show()
            }
        }
        else {
            val numNotifications = editEventViewModel.eventNotifications.size
            for (i in numNotifications until 5) {
                editEventViewModel.eventNotifications.add(Date(0L))
            }

            editEventViewModel.event?.notification1_time = editEventViewModel.eventNotifications[0]
            editEventViewModel.event?.notification2_time = editEventViewModel.eventNotifications[1]
            editEventViewModel.event?.notification3_time = editEventViewModel.eventNotifications[2]
            editEventViewModel.event?.notification4_time = editEventViewModel.eventNotifications[3]
            editEventViewModel.event?.notification5_time = editEventViewModel.eventNotifications[4]

            editEventViewModel.event?.let {
                it.name = eventName.text.toString()
                it.time = editEventViewModel.eventDate
                it.description = eventDescription.text.toString()
                it.type = when (eventTypeRadioGroup.checkedRadioButtonId) {
                    R.id.edit_event_exam_radio -> "Exam"
                    R.id.edit_event_event_radio -> "Event"
                    else -> "Assignment"
                }
                if (courseSpinner.selectedItemPosition <= 0) {
                    it.courseId = UUID.randomUUID()
                }
                else {
                    it.courseId = editEventViewModel.allCourses.get(courseSpinner.selectedItemPosition - 1).id
                }

                val eventIds = List<Int>(5) { i -> it.id.leastSignificantBits.toInt() + i }
                val notificationTitle = "${it.type} - ${it.name}"
                callbacks?.createNotifications(notificationTitle, it.description, editEventViewModel.eventNotifications, eventIds)
            }

            editEventViewModel.event?.let {
                editEventViewModel.updateEvent(it)
            }

            activity?.onBackPressed()
        }
    }

    private fun updateUICourseList() {
        val spinnerVals = MutableList<String>(editEventViewModel.allCourses.size) {i -> editEventViewModel.allCourses[i].name}
        spinnerVals.add(0, "None")
        val adapter = activity?.let { ArrayAdapter(it, R.layout.spinner_item, spinnerVals) }
        courseSpinner.adapter = adapter
        var spinnerIdx = 0
        for (cIdx in editEventViewModel.allCourses.indices) {
            if (editEventViewModel.event?.courseId == editEventViewModel.allCourses[cIdx].id) {
                spinnerIdx = cIdx + 1
                break
            }
        }
        courseSpinner.setSelection(spinnerIdx)
    }

    private fun updateUINotifications() {
        notificationContainer.removeAllViewsInLayout()
        if (editEventViewModel.eventNotifications.size == 0) {
            noNotificationsText.visibility = View.VISIBLE
        }
        else {
            noNotificationsText.visibility = View.GONE
        }
        for (idx in 0 until 5) {
            if (idx < editEventViewModel.eventNotifications.size && editEventViewModel.eventNotifications[idx].time != 0L) {
                val notificationDate = editEventViewModel.eventNotifications[idx]
                val newView = LayoutInflater.from(activity).inflate(R.layout.event_notification_view, null)
                val timeBtn = newView.findViewById<Button>(R.id.notification_view_time)
                val dateBtn = newView.findViewById<Button>(R.id.notification_view_date)
                val deleteBtn = newView.findViewById<ImageButton>(R.id.notification_view_delete)

                timeBtn.text = SimpleDateFormat(time_format_string).format(notificationDate)
                dateBtn.text = SimpleDateFormat(date_format_string).format(notificationDate)

                timeBtn.setOnClickListener {
                    activity?.let {
                        EventNotificationTimePickerFragment.newInstance(notificationDate, idx).apply {
                            setTargetFragment(this@EditEventFragment, REQUEST_DATE)
                            show(it.supportFragmentManager, "get_date")
                        }
                    }
                }
                dateBtn.setOnClickListener {
                    activity?.let {
                        EventNotificationDatePickerFragment.newInstance(notificationDate, idx).apply {
                            setTargetFragment(this@EditEventFragment, REQUEST_DATE)
                            show(it.supportFragmentManager, "get_date")
                        }
                    }
                }
                deleteBtn.setOnClickListener {
                    editEventViewModel.eventNotifications.removeAt(idx)
                    updateUINotifications()
                }

                notificationContainer.addView(newView)
            }
            else {
                break
            }
        }
    }

    override fun onEventDateSelected(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = editEventViewModel.eventDate
        val oldHour = calendar.get(Calendar.HOUR_OF_DAY)
        val oldMin = calendar.get(Calendar.MINUTE)
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, oldHour)
        calendar.set(Calendar.MINUTE, oldMin)
        calendar.set(Calendar.SECOND, 0)
        editEventViewModel.eventDate = calendar.time
        editDateButton.text = SimpleDateFormat(date_format_string).format(calendar.time)
    }

    override fun onEventTimeSelected(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            this.time = editEventViewModel.eventDate
            this.set(Calendar.HOUR_OF_DAY, hour)
            this.set(Calendar.MINUTE, minute)
            this.set(Calendar.SECOND, 0)
        }
        editEventViewModel.eventDate = calendar.time
        editTimeButton.text = SimpleDateFormat(time_format_string).format(editEventViewModel.eventDate)
    }

    override fun onNotificationDateSelected(date: Date, notificationIdx: Int) {
        val calendar = Calendar.getInstance().apply {
            time = editEventViewModel.eventNotifications[notificationIdx]
            val oldHour = this.get(Calendar.HOUR_OF_DAY)
            val oldMin = this.get(Calendar.MINUTE)
            time = date
            this.set(Calendar.HOUR_OF_DAY, oldHour)
            this.set(Calendar.MINUTE, oldMin)
            this.set(Calendar.SECOND, 0)
        }
        editEventViewModel.eventNotifications[notificationIdx] = calendar.time
        updateUINotifications()
    }

    override fun onNotificationTimeSelected(hour: Int, minute: Int, notificationIdx: Int) {
        val cal = Calendar.getInstance().apply {
            time = editEventViewModel.eventNotifications[notificationIdx]
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        editEventViewModel.eventNotifications[notificationIdx] = cal.time
        updateUINotifications()
    }

    private fun fieldsFilled(): Boolean {
        return (eventName.text.isNotEmpty())
    }

    companion object {
        fun newInstance(eventId: UUID, newEvent: Boolean): EditEventFragment {
            val args = Bundle().apply {
                putSerializable(EVENT_ID_KEY, eventId)
                putSerializable(NEW_EVENT_KEY, newEvent)
            }
            return EditEventFragment().apply {
                arguments = args
            }
        }
    }
}