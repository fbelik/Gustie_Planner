package com.bignerdranch.android.gustieplanner

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val DATE = "date"
private const val NOTIFICATION_IDX = "notification_idx"

class EventNotificationTimePickerFragment: DialogFragment() {

    interface Callbacks {
        fun onNotificationTimeSelected(hour: Int, minute: Int, notificationIdx: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val notificationIdx = arguments?.get(NOTIFICATION_IDX) as Int

        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour: Int, minute: Int ->

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onNotificationTimeSelected(hour, minute, notificationIdx)
            }
        }

        val date = arguments?.get(DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
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
        fun newInstance(date: Date, notificationIdx: Int): EventNotificationTimePickerFragment {
            val args = Bundle().apply {
                putSerializable(DATE, date)
                putSerializable(NOTIFICATION_IDX, notificationIdx)
            }
            return EventNotificationTimePickerFragment().apply {
                arguments = args
            }
        }
    }
}