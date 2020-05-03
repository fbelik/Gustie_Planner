package com.bignerdranch.android.gustieplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class EditCourseViewModel: ViewModel() {
    private val courseRepository = CourseRepository.get()
    private val courseIdLiveData = MutableLiveData<UUID>()

    var courseLiveData: LiveData<Course?> =
        Transformations.switchMap(courseIdLiveData) { courseId ->
            courseRepository.getCourse(courseId)
        }

    fun loadCourse(id: UUID) {
        courseIdLiveData.value = id
    }

    fun updateCourse(course: Course) {
        courseRepository.updateCourse(course)
    }

    fun deleteCourse(courseId: UUID) {
        courseRepository.deleteCourse(courseId)
    }
}