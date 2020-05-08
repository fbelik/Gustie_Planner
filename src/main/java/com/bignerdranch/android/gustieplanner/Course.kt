package com.bignerdranch.android.gustieplanner

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.StringBuilder
import java.util.*

@Entity
class Course(@PrimaryKey val id: UUID = UUID.randomUUID(),
             var name: String = "",
             var departmentCode: String = "",
             var numberCode: String = "",
             var startTime: Int = -1,
             var courseTime: Float = 1f,
             var isOnM: Boolean = false,
             var isOnT: Boolean = false,
             var isOnW: Boolean = false,
             var isOnR: Boolean = false,
             var isOnF: Boolean = false,
             var color: Int = 0xff0000) {

    fun startTimeToString(): String {
        var hour = startTime / 60
        val minute = (((startTime / 60f) - hour) * 60).toInt()
        var amPm = "am"
        if (hour >= 12) {
            amPm = "pm"
            if (hour > 12) {
                hour -= 12
            }
        }
        return "%d:%02d $amPm".format(hour, minute)
    }

    fun endTimeToString(): String {
        val endTime = startTime + (courseTime * 60).toInt()
        var hour = endTime / 60
        var minute = (((endTime / 60f) - hour) * 60).toInt()
        var amPm = "am"
        if (hour >= 12) {
            amPm = "pm"
            if (hour > 12) {
                hour -= 12
            }
        }
        return "%d:%02d $amPm".format(hour, minute)
    }

    override fun toString(): String {
        val daysOfWeek = StringBuilder()
        val initialString = "("
        daysOfWeek.append(initialString)
        if (isOnM) {
            daysOfWeek.append('M')
        }
        if (isOnT) {
            daysOfWeek.append('T')
        }
        if (isOnW) {
            daysOfWeek.append('W')
        }
        if (isOnR) {
            daysOfWeek.append('R')
        }
        if (isOnF) {
            daysOfWeek.append('F')
        }
        var hour = startTime / 60
        var minute = (((startTime / 60f) - hour) * 60).toInt()
        var amPm = "am"
        if (hour >= 12) {
            amPm = "pm"
            if (hour > 12) {
                hour -= 12
            }
        }
        if (daysOfWeek.toString().length > 1) {
            daysOfWeek.append(" ")
        }
        daysOfWeek.append("%d:%02d$amPm)".format(hour, minute))
        return "$departmentCode-$numberCode $name $daysOfWeek"
    }
}
