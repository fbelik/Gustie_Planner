package com.bignerdranch.android.gustieplanner

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "ProfileActivity"

class ProfileActivity: AppCompatActivity(), EditCourseFragment.Callbacks {

    private lateinit var nameText: EditText
    private lateinit var setNameButton: Button
    private lateinit var newCourseButton: ImageButton
    private lateinit var backButton: Button
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var notFragmentContainer: LinearLayout
    private lateinit var courseListContainer: LinearLayout
    private lateinit var noCoursesText: TextView

    private val profileViewModel : ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    private lateinit var sharedPreferences: SharedPreferences
    private var usersName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        nameText = findViewById(R.id.input_name_text)
        setNameButton = findViewById(R.id.set_name_btn)
        newCourseButton = findViewById(R.id.new_course_btn)
        backButton = findViewById(R.id.profile_back_button)
        fragmentContainer = findViewById(R.id.profile_fragment_container)
        notFragmentContainer = findViewById(R.id.profile_not_fragment)
        courseListContainer = findViewById(R.id.course_list_container)
        noCoursesText = findViewById(R.id.no_courses_text)

        sharedPreferences = getSharedPreferences(Keys.sharedPreferencesKey, Context.MODE_PRIVATE)

        retrieveName()
        nameText.setText(usersName)

        setNameButton.setOnClickListener {
            if (nameText.text.isNotEmpty()) {
                saveName(nameText.text.toString())
            }
            else {
                val alert = AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Must enter a name")
                    setCancelable(false)
                    setPositiveButton("Ok") {dialog, id ->
                        Log.d(TAG,"Invalid entry")
                    }
                    show()
                }
            }
        }

        newCourseButton.setOnClickListener {
            val newCourse = Course()
            profileViewModel.addCourse(newCourse)
            val fragment = EditCourseFragment.newInstance(newCourse.id, isNew = true)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.profile_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val coursesObserver = Observer<List<Course>> {courses ->
            updateUI(courses)
        }

        profileViewModel.coursesLiveData.observe(this, coursesObserver)
    }

    override fun created(boolean: Boolean) {
        notFragmentContainer.visibility = if (!boolean) View.VISIBLE else View.GONE
    }

    private fun updateUI(courses: List<Course>) {
        Log.d(TAG, "Updating UI, total of ${courses.size} courses")
        courseListContainer.removeAllViews()
        val courses = courses
        if (courses.isEmpty()) {
            noCoursesText.visibility = View.VISIBLE
        }
        else {
            noCoursesText.visibility = View.INVISIBLE
            for (course in courses) {
                val newView = LayoutInflater.from(this).inflate(R.layout.course_view, null)
                val titleText = newView.findViewById<TextView>(R.id.course_list_title)
                val courseColor = newView.findViewById<View>(R.id.course_color_view)
                val deleteButton = newView.findViewById<ImageButton>(R.id.course_list_delete)
                titleText.text = course.toString()
                courseColor.setBackgroundColor(course.color)
                deleteButton.setOnClickListener {
                    profileViewModel.deleteCourse(course.id)
                }

                titleText.setOnClickListener {
                    val fragment = EditCourseFragment.newInstance(course.id, isNew = false)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.profile_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                courseListContainer.addView(newView)
            }
        }
    }

    private fun retrieveName() {
        usersName = sharedPreferences.getString(Keys.nameKey, "") ?: ""
    }

    private fun saveName(newName: String) {
        val editor = sharedPreferences.edit()
        editor.putString(Keys.nameKey, newName)
        editor.apply()
    }
}
