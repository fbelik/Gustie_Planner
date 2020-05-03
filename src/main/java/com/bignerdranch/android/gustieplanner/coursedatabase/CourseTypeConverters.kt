package com.bignerdranch.android.gustieplanner.coursedatabase

import androidx.room.TypeConverter
import java.util.*

class CourseTypeConverters {

    @TypeConverter
    fun toUUID(uuid: String): UUID = UUID.fromString(uuid)

    @TypeConverter
    fun fromUUID(uuid: UUID): String = uuid.toString()

}