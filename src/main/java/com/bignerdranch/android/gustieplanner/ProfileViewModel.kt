package com.bignerdranch.android.gustieplanner

import androidx.lifecycle.ViewModel
import java.util.*

class ProfileViewModel: ViewModel() {
    private val courseRepository = CourseRepository.get()
    var coursesLiveData = courseRepository.getCourses()

    fun addCourse(course: Course) {
        courseRepository.addCourse(course)
        coursesLiveData = courseRepository.getCourses()
    }

    fun deleteCourse(id: UUID) {
        courseRepository.deleteCourse(id)
        coursesLiveData = courseRepository.getCourses()
    }
}