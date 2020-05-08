package com.bignerdranch.android.gustieplanner.eventdatabase

import androidx.room.TypeConverter
import java.util.*

class EventTypeConverters {

    @TypeConverter
    fun toUUID(uuid: String): UUID = UUID.fromString(uuid)

    @TypeConverter
    fun fromUUID(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toDate(date: Long): Date = Date(date)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

}
