package com.bignerdranch.android.gustieplanner

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.*

private const val TAG = "ProfileActivity"
private const val MAX_NAME_LENGTH = 15

class ProfileFragment: Fragment() {

    private lateinit var nameText: EditText
    private lateinit var setNameButton: Button
    private lateinit var newCourseButton: ImageButton
    private lateinit var backButton: Button
    private lateinit var courseListContainer: LinearLayout
    private lateinit var noCoursesText: TextView

    private val profileViewModel : ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    interface Callbacks {
        fun onEditCourse(id: UUID, isNew: Boolean)
    }

    private var callbacks: Callbacks? = null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        nameText = view.findViewById(R.id.input_name_text)
        setNameButton = view.findViewById(R.id.set_name_btn)
        newCourseButton = view.findViewById(R.id.new_course_btn)
        backButton = view.findViewById(R.id.profile_back_button)
        courseListContainer = view.findViewById(R.id.course_list_container)
        noCoursesText = view.findViewById(R.id.no_courses_text)

        return view

    }

    override fun onStart() {
        super.onStart()
        sharedPreferences = activity!!.getSharedPreferences(Keys.sharedPreferencesKey, Context.MODE_PRIVATE)
        var usersName = sharedPreferences.getString(Keys.nameKey, "") ?: ""
        if (usersName.length > MAX_NAME_LENGTH) {
            usersName = usersName.slice(0 until MAX_NAME_LENGTH)
        }
        nameText.setText(usersName)

        setNameButton.setOnClickListener {
            val name = nameText.text
            if (name.length > MAX_NAME_LENGTH) {
                Toast.makeText(context, "Exceeds maximum name length $MAX_NAME_LENGTH", Toast.LENGTH_LONG).show()
            }
            else if (name.isNotEmpty()) {
                saveName(nameText.text.toString())
                Toast.makeText(context, "Name saved", Toast.LENGTH_LONG).show()
            }
            else {
                AlertDialog.Builder(activity).apply {
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
            callbacks?.onEditCourse(newCourse.id, true)
        }

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        val coursesObserver = Observer<List<Course>> {courses ->
            updateUI(courses)
        }

        profileViewModel.coursesLiveData.observe(this, coursesObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        callbacks = null
    }

    private fun updateUI(courses: List<Course>) {
        Log.d(TAG, "Updating UI, total of ${courses.size} courses")
        courseListContainer.removeAllViews()
        if (courses.isEmpty()) {
            noCoursesText.visibility = View.VISIBLE
        }
        else {
            noCoursesText.visibility = View.INVISIBLE
            for (course in courses) {
                val newView = LayoutInflater.from(activity).inflate(R.layout.course_view, null)
                val linearLayout = newView.findViewById<LinearLayout>(R.id.course_list_layout)
                val titleText = newView.findViewById<TextView>(R.id.course_list_title)
                val deleteButton = newView.findViewById<ImageButton>(R.id.course_list_delete)
                linearLayout.background.setTint(course.color)
                titleText.text = course.toString()
                deleteButton.setOnClickListener {
                    profileViewModel.deleteCourse(course.id)
                }

                linearLayout.setOnClickListener {
                    callbacks?.onEditCourse(course.id, isNew = false)
                }

                newView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0,0,0,10)
                }

                courseListContainer.addView(newView)
            }
        }
    }

    private fun saveName(newName: String) {
        val editor = sharedPreferences.edit()
        editor.putString(Keys.nameKey, newName)
        editor.apply()
    }
}

