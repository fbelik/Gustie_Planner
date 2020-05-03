package com.bignerdranch.android.gustieplanner

import androidx.lifecycle.ViewModel

class CourseScheduleViewModel: ViewModel() {

    private val courseRepository = CourseRepository.get()
    var coursesLiveData = courseRepository.getCourses()

}
