package com.bignerdranch.android.gustieplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class EditEventViewModel: ViewModel() {

    private val eventRepository = EventRepository.get()
    private val eventIdLiveData = MutableLiveData<UUID>()

    var eventLiveData: LiveData<Event?> =
        Transformations.switchMap(eventIdLiveData) { eventId ->
            eventRepository.getEvent(eventId)
        }

    fun updateEvent(event: Event) {
        eventRepository.updateEvent(event)
    }

    fun loadEvent(id: UUID) {
        eventIdLiveData.value = id
    }

    fun deleteEvent(id: UUID) {
        eventRepository.deleteEvent(id)
    }

    private val courseRepository = CourseRepository.get()
    var coursesLiveData = courseRepository.getCourses()

    fun getCourse(id: UUID): Course? {
        return courseRepository.getCourse(id).value
    }

}