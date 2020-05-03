package com.bignerdranch.android.gustieplanner

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Event(@PrimaryKey val id: UUID,
            val name: String = "",
            val time: Date = Date(),
            val type: String = "Assignment",
            val courseId: UUID? = UUID.randomUUID(),
            val notification1_time: Date? = null,
            val notification2_time: Date? = null,
            val notification3_time: Date? = null,
            val notification4_time: Date? = null,
            val notification5_time: Date? = null) {

}