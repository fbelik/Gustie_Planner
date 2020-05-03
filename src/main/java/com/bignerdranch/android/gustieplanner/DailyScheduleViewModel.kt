package com.bignerdranch.android.gustieplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class DailyScheduleViewModel: ViewModel() {

    private val courseRepository = CourseRepository.get()
    var coursesLiveData = courseRepository.getCourses()

}