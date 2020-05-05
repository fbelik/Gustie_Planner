package com.bignerdranch.android.gustieplanner

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val DATE = "date"

class EditEventTimePickerFragment: DialogFragment() {

    interface Callbacks {
        fun onTimeSelected(hour: Int, minute: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour: Int, minute: Int ->

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onTimeSelected(hour, minute)
            }
        }

        val date = arguments?.get(DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialHour = calendar.get(Calendar.HOUR)
        val initialMinute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true
        )
    }

    companion object {
        fun newInstance(date: Date): EditEventTimePickerFragment {
            val args = Bundle().apply {
                putSerializable(DATE, date)
            }
            return EditEventTimePickerFragment().apply {
                arguments = args
            }
        }
    }
}