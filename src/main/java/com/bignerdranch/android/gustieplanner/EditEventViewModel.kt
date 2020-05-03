package com.bignerdranch.android.gustieplanner

import androidx.lifecycle.ViewModel
import java.util.*

class EditEventViewModel: ViewModel() {

    private val courseRepository = CourseRepository.get()
    var courses = courseRepository.getCourses()

    fun getCourse(id: UUID): Course? {
        return courseRepository.getCourse(id).value
    }

}