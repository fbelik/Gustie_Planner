package com.bignerdranch.android.gustieplanner

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val DATE = "date"
private const val NOTIFICATION_IDX = "notification_idx"

class EventNotificationDatePickerFragment: DialogFragment() {

    interface Callbacks {
        fun onNotificationDateSelected(date: Date, notificationIdx: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val notificationIdx = arguments?.get(NOTIFICATION_IDX) as Int

        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year: Int, month: Int, day: Int ->

            val resultDate: Date = GregorianCalendar(year,month,day).time

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onNotificationDateSelected(resultDate, notificationIdx)
            }
        }

        val date = arguments?.get(DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        fun newInstance(date: Date, notificationIdx: Int): EventNotificationDatePickerFragment {
            val args = Bundle().apply {
                putSerializable(DATE, date)
                putSerializable(NOTIFICATION_IDX, notificationIdx)
            }
            return EventNotificationDatePickerFragment().apply {
                arguments = args
            }
        }
    }

}