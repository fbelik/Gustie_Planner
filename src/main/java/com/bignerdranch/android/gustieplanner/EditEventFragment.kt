package com.bignerdranch.android.gustieplanner

import android.app.AlertDialog
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
private const val MAX_TITLE_LEN = 30

class EditEventFragment: Fragment(), EditEventDatePickerFragment.Callbacks, EditEventTimePickerFragment.Callbacks {

    private lateinit var eventName: EditText
    private lateinit var courseSpinner: Spinner
    private lateinit var eventDescription: EditText
    private lateinit var editDateButton: Button
    private lateinit var editTimeButton: Button
    private lateinit var eventTypeRadioGroup: RadioGroup
    private lateinit var newNotificationButton: ImageButton
    private lateinit var submitBtn: Button

    private var event: Event? = null
    private lateinit var editEventUUID: UUID
    private var eventDate = Date()
    private var allCourses: List<Course> = listOf()
    private var isNew = false

    interface Callbacks {
        fun isCreated(bool: Boolean)

    }

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
        submitBtn = view.findViewById(R.id.edit_event_submit)

        editEventUUID = arguments?.getSerializable(EVENT_ID_KEY) as UUID

        return view
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

        editDateButton.setOnClickListener {
            activity?.let {
                EditEventDatePickerFragment.newInstance(eventDate).apply {
                    setTargetFragment(this@EditEventFragment, REQUEST_DATE)
                    show(it.supportFragmentManager, "get_date")
                }
            }
        }

        editTimeButton.setOnClickListener {
            activity?.let {
                EditEventTimePickerFragment.newInstance(eventDate).apply {
                    setTargetFragment(this@EditEventFragment, REQUEST_TIME)
                    show(it.supportFragmentManager, "get_time")
                }
            }
        }

        newNotificationButton.setOnClickListener {
            //TODO add a new notification here
        }

        submitBtn.setOnClickListener {
            onSubmit()
        }

        eventTypeRadioGroup.check(R.id.edit_event_assignment_radio)

        isNew = arguments?.getSerializable(NEW_EVENT_KEY) as Boolean
        val eventId = arguments?.getSerializable(EVENT_ID_KEY) as UUID

        editEventViewModel.coursesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { courses ->
            allCourses = courses
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
                    event = it
                    eventDate = it.time
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
                    courseSpinner.adapter?.apply {
                        var spinnerIdx = 0
                        for (cIdx in allCourses.indices) {
                            if (it.courseId == allCourses[cIdx].id) {
                                spinnerIdx = cIdx + 1
                                break
                            }
                        }
                        courseSpinner.setSelection(spinnerIdx)
                    }
                    if (it.notification1_time != null) {
                        //TODO add notifications
                    }
                    if (it.notification2_time != null) {
                        //TODO
                    }
                    if (it.notification3_time != null) {
                        //TODO
                    }
                    if (it.notification4_time != null) {
                        //TODO
                    }
                    if (it.notification5_time != null) {
                        //TODO
                    }
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        if (!fieldsFilled() && isNew) {
            editEventViewModel.deleteEvent(editEventUUID)
        }
    }

    private fun onSubmit() {
        if (!fieldsFilled()) {
            val alert = AlertDialog.Builder(context).apply {
                setTitle("Error")
                setMessage("One of the fields was not filled")
                setCancelable(false)
                setPositiveButton("Ok") { dialog, id ->
                }
                show()
            }
        }
        else {
            event?.let {
                it.name = eventName.text.toString()
                it.time = eventDate
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
                    it.courseId = allCourses.get(courseSpinner.selectedItemPosition - 1).id
                }
                editEventViewModel.updateEvent(it)
            }

            activity?.onBackPressed()
        }
    }

    private fun updateUICourseList() {
        val spinnerVals = MutableList<String>(allCourses.size) {i -> allCourses[i].name}
        spinnerVals.add(0, "None")
        val adapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, spinnerVals) }
        courseSpinner.adapter = adapter
        var spinnerIdx = 0
        for (cIdx in allCourses.indices) {
            if (event?.courseId == allCourses[cIdx].id) {
                spinnerIdx = cIdx + 1
                break
            }
        }
        courseSpinner.setSelection(spinnerIdx)
    }

    override fun onDateSelected(date: Date) {
        eventDate = date
        editDateButton.text = SimpleDateFormat(date_format_string).format(date)
    }

    override fun onTimeSelected(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = eventDate
        calendar.set(Calendar.HOUR, hour)
        calendar.set(Calendar.MINUTE, minute)
        eventDate = calendar.time
        editTimeButton.text = SimpleDateFormat(time_format_string).format(eventDate)
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