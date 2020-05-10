package com.bignerdranch.android.gustieplanner

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Event(@PrimaryKey val id: UUID = UUID.randomUUID(),
            var name: String = "",
            var time: Date = Date(),
            var type: String = "Assignment",
            var courseId: UUID = UUID.randomUUID(),
            var description: String = "",
//            var notificationId: Int = 0,
            var notification1_time: Date = Date(0),
            var notification2_time: Date = Date(0),
            var notification3_time: Date = Date(0),
            var notification4_time: Date = Date(0),
            var notification5_time: Date = Date(0)) {

}