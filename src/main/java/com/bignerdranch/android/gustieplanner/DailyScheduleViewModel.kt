package com.bignerdranch.android.gustieplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class DailyScheduleViewModel: ViewModel() {

    private val eventRepository = EventRepository.get()
    var eventsLiveData = eventRepository.getEvents()

    fun addEvent(event: Event) {
        eventRepository.addEvent(event)
    }

    fun deleteEvent(eventId: UUID) {
        eventRepository.deleteEvent(eventId)
    }

    private val courseRepository = CourseRepository.get()
    var coursesLiveData = courseRepository.getCourses()

}