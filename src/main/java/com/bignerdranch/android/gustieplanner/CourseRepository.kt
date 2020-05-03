package com.bignerdranch.android.gustieplanner

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.gustieplanner.coursedatabase.CourseDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

private const val DATABASE_NAME = "course_database"

class CourseRepository private constructor(context: Context){

    private val database: CourseDatabase = Room.databaseBuilder(
        context.applicationContext,
        CourseDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val dao = database.courseDao()
    private val executor = Executors.newSingleThreadExecutor()
    // private val filesDir = context.applicationContext.filesDir

    //fun getCourses(): List<Course> = dao.getCourses()
    fun getCourses(): LiveData<List<Course>> {
        val callable = Callable { dao.getCourses() }
        val future = executor.submit(callable)
        return future.get()
    }

    fun getCourse(id: UUID): LiveData<Course?> {
        val callable = Callable { dao.getCourse(id) }
        val future = executor.submit(callable)
        return future.get()
    }

    fun updateCourse(course: Course) {
        executor.execute {
            dao.updateCourse(course)
        }
    }

    fun addCourse(course: Course) {
        executor.execute {
            dao.addCourse(course)
        }
    }
    fun deleteCourse(id: UUID) {
        executor.execute {
            dao.deleteCourse(id)
        }
    }

    companion object {
        private var INSTANCE: CourseRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CourseRepository(context)
            }
        }

        fun get(): CourseRepository {
            return INSTANCE ?: throw IllegalStateException("Course Repository must be initialized")
        }
    }

}