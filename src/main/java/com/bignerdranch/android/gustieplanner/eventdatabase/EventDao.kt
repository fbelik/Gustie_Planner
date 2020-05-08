package com.bignerdranch.android.gustieplanner.eventdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.gustieplanner.Event
import java.util.*

@Dao
interface EventDao {

    @Query("SELECT * FROM event ORDER BY time")
    fun getEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE id=(:id)")
    fun getEvent(id: UUID): LiveData<Event?>

    @Update
    fun updateEvent(event: Event)

    @Insert
    fun addEvent(event: Event)

    @Query("DELETE FROM event WHERE id=(:id)")
    fun deleteEvent(id: UUID)

}
