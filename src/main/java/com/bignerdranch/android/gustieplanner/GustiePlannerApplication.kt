package com.bignerdranch.android.gustieplanner

import android.app.Application

class GustiePlannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        CourseRepository.initialize(this)
        EventRepository.initialize(this)
    }

}