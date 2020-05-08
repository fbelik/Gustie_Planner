package com.bignerdranch.android.gustieplanner

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.gustieplanner.eventdatabase.EventDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

private const val DATABASE_NAME = "event_database"
class EventRepository private constructor(context: Context) {

    private val database: EventDatabase = Room.databaseBuilder(
        context.applicationContext,
        EventDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val dao = database.eventDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getEvents(): LiveData<List<Event>> {
        val callable = Callable { dao.getEvents() }
        val future = executor.submit(callable)
        return future.get()
    }

    fun getEvent(id: UUID): LiveData<Event?> {
        val callable = Callable { dao.getEvent(id) }
        val future = executor.submit(callable)
        return future.get()
    }

    fun updateEvent(event: Event) {
        executor.execute {
            dao.updateEvent(event)
        }
    }

    fun addEvent(event: Event) {
        executor.execute {
            dao.addEvent(event)
        }
    }

    fun deleteEvent(id: UUID) {
        executor.execute {
            dao.deleteEvent(id)
        }
    }

    companion object {
        private var INSTANCE: EventRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = EventRepository(context)
            }
        }

        fun get(): EventRepository {
            return EventRepository.INSTANCE ?: throw IllegalStateException("Event Repository must be initialized")
        }
    }

}