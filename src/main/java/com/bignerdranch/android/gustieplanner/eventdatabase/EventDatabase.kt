package com.bignerdranch.android.gustieplanner.eventdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.gustieplanner.Event

@Database(entities = [ Event::class ], version = 1)
@TypeConverters(EventTypeConverters::class)
abstract class EventDatabase: RoomDatabase() {

    abstract fun eventDao(): EventDao

}