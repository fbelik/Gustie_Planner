package com.bignerdranch.android.gustieplanner

import android.os.Bundle
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

class EditEventFragment: Fragment(), EditEventDatePickerFragment.Callbacks, EditEventTimePickerFragment.Callbacks {

    private lateinit var eventName: EditText
    private lateinit var courseSpinner: Spinner
    private lateinit var eventDescription: EditText
    private lateinit var editDateButton: Button
    private lateinit var editTimeButton: Button
    private lateinit var eventTypeRadioGroup: RadioGroup
    private lateinit var newNotificationButton: ImageButton

    private var eventDate = Date()
    private var isNewEvent = false

    private var editEventUUID: UUID? = null

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

        editEventUUID = arguments?.let {
            it.getSerializable(EVENT_ID_KEY) as UUID
        }

        val spinnerVals = arrayOf("None")
        val adapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, spinnerVals) }
        courseSpinner.adapter = adapter

        isNewEvent = arguments?.getSerializable(NEW_EVENT_KEY) as Boolean
        // Set values of input boxes

        if (!isNewEvent) {
        }


        return view
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