package com.bignerdranch.android.gustieplanner.coursedatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.gustieplanner.Course
import java.util.*

@Dao
interface CourseDao {

    @Query("SELECT * FROM course ORDER BY startTime")
    fun getCourses(): LiveData<List<Course>>

    @Query("SELECT * FROM course WHERE id=(:id)")
    fun getCourse(id: UUID): LiveData<Course?>

    @Update
    fun updateCourse(course: Course)

    @Insert
    fun addCourse(course: Course)

    @Query("DELETE FROM course WHERE id=(:id)")
    fun deleteCourse(id: UUID)

}
