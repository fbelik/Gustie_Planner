package com.bignerdranch.android.gustieplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*

private const val EVENT_ID_KEY = "event_id"

class EditEventFragment: Fragment() {

    private lateinit var eventName: EditText
    private lateinit var courseSpinner: Spinner
    private lateinit var eventDescription: EditText

    private var editEventUUID: UUID? = null

    interface Callbacks {
        fun onSaveEvent(event: Event)
    }

    var callbacks: Callbacks? = null

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

        editEventUUID = arguments?.let {
            it.getSerializable(EVENT_ID_KEY) as UUID
        }

        val spinnerVals = arrayOf("None")
        val adapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, spinnerVals) }
        courseSpinner.adapter = adapter

        // Set values of input boxes



        return view
    }

    companion object {
        fun newInstance(eventId: UUID): EditEventFragment {
            val args = Bundle().apply {
                putSerializable(EVENT_ID_KEY, eventId)
            }
            return EditEventFragment().apply {
                arguments = args
            }
        }
    }
}