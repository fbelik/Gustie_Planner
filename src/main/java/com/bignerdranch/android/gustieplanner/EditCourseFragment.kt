package com.bignerdranch.android.gustieplanner

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorListener
import java.lang.NumberFormatException
import java.util.*

private const val TAG = "NewCourseFragment"
private const val COURSE_ID_TAG = "course_id"
private const val IS_NEW_TAG = "course_is_new"
private const val MAX_TITLE_LEN = 25

class EditCourseFragment: Fragment() {

    private lateinit var courseNameText: EditText
    private lateinit var departmentCode: EditText
    private lateinit var numberCode: EditText
    private lateinit var startHour: EditText
    private lateinit var startMinute: EditText
    private lateinit var startAmPm: ToggleButton
    private lateinit var courseLength: EditText
    private lateinit var mondayBtn: ToggleButton
    private lateinit var tuesdayBtn: ToggleButton
    private lateinit var wednesdayBtn: ToggleButton
    private lateinit var thursdayBtn: ToggleButton
    private lateinit var fridayBtn: ToggleButton
    private lateinit var colorSelectBtn: Button
    private lateinit var colorLookBtn: Button
    private lateinit var submitBtn: Button

    private var courseColor: Int? = null

    private var course: Course? = null
    private var isNew = false

    private val courseViewModel : EditCourseViewModel by lazy {
        ViewModelProviders.of(this).get(EditCourseViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_course, container, false)

        courseNameText = view.findViewById(R.id.course_name_edittxt)
        departmentCode = view.findViewById(R.id.department_code_edittxt)
        numberCode = view.findViewById(R.id.number_code_edittxt)
        startHour = view.findViewById(R.id.course_start_hour)
        startMinute = view.findViewById(R.id.course_start_minute)
        startAmPm = view.findViewById(R.id.course_start_ampm)
        courseLength = view.findViewById(R.id.course_num_hours)
        mondayBtn = view.findViewById(R.id.course_monday_button)
        tuesdayBtn = view.findViewById(R.id.course_tuesday_button)
        wednesdayBtn = view.findViewById(R.id.course_wednesday_button)
        thursdayBtn = view.findViewById(R.id.course_thursday_button)
        fridayBtn = view.findViewById(R.id.course_friday_button)
        colorSelectBtn = view.findViewById(R.id.course_color_select_button)
        colorLookBtn = view.findViewById(R.id.course_color_look_button)
        submitBtn = view.findViewById(R.id.submit_course)

        return view
    }

    override fun onStart() {
        super.onStart()
        courseNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length > MAX_TITLE_LEN) {
                    courseNameText.setText(text.slice(0 until MAX_TITLE_LEN))
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        departmentCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length > 3) {
                    departmentCode.setText(text.slice(0 until 3))
                }
                else if (text.length == 3) {
                    numberCode.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        numberCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 3) {
                    numberCode.setText(s?.slice(0 until 3))
                }
                else if (s.toString().length == 3) {
                    startHour.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        startHour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    startHour.setText(s?.slice(0 until 2))
                }
                else if (s.toString().length == 2) {
                    startMinute.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        startMinute.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    startMinute.setText(s?.slice(0 until 2))
                }
                else if (s.toString().length == 2) {
                    closeKeyboard()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        courseLength.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val txt = s.toString()
                val value = txt.toFloatOrNull() ?: 0f
                if (value > 24f) {
                    courseLength.setText("24.0")
                    Toast.makeText(context, "Course cannot be longer than 24 hours", Toast.LENGTH_LONG).show()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        colorSelectBtn.setOnClickListener {
            ColorPickerDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK).apply {
                setTitle("Course color picker")
                setPositiveButton("Ok", ColorListener { color, fromUser ->
                    Log.d("Color", "Setting color button to $color")
                    courseColor = color
                    colorLookBtn.setBackgroundColor(color)
                })
                setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
                    dialog?.dismiss()
                }
                show()
            }
        }

        colorLookBtn.setOnClickListener {
            ColorPickerDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK).apply {
                setTitle("Course color picker")
                setPositiveButton("Ok", ColorListener { color, fromUser ->
                    Log.d("Color", "Setting color button to $color")
                    courseColor = color
                    colorLookBtn.setBackgroundColor(color)
                })
                setNegativeButton("Cancel", DialogInterface.OnClickListener() { dialog: DialogInterface?, which: Int ->
                    dialog?.dismiss()
                })
                show()
            }
        }

        submitBtn.setOnClickListener {
            onSubmit()
        }

        courseViewModel.loadCourse(arguments?.getSerializable(COURSE_ID_TAG) as UUID)
        isNew = arguments?.getSerializable(IS_NEW_TAG) as Boolean
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseViewModel.courseLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { incomingCourse ->
                incomingCourse?.let {
                    course = it
                    Log.d(TAG, "Initializing UI")
                    courseNameText.setText(it.name)
                    departmentCode.setText(it.departmentCode)
                    numberCode.setText(it.numberCode)
                    if (it.startTime != -1) {
                        val courseStartInfo = intToHrMin(it.startTime)
                        startHour.setText("%02d".format(courseStartInfo[0]))
                        startMinute.setText("%02d".format(courseStartInfo[1]))
                        startAmPm.isChecked = courseStartInfo[2] == 1
                    }
                    courseLength.setText(it.courseTime.toString())
                    mondayBtn.isChecked = it.isOnM
                    tuesdayBtn.isChecked = it.isOnT
                    wednesdayBtn.isChecked = it.isOnW
                    thursdayBtn.isChecked = it.isOnR
                    fridayBtn.isChecked = it.isOnF
                    colorLookBtn.setBackgroundColor(it.color)
                    courseColor = it.color
                }
            })
    }

    override fun onStop() {
        super.onStop()
        if (!fieldsFilled() && isNew) {
            courseViewModel.deleteCourse(course!!.id)
        }
    }

    private fun onSubmit() {
        if (!fieldsFilled()) {
            AlertDialog.Builder(context).apply {
                setTitle("Error")
                setMessage("One of the fields was not filled")
                setCancelable(false)
                setPositiveButton("Ok") { dialog, id ->
                    Log.d(TAG, "Invalid entry")
                }
                show()
            }
            return
        }
        try {
            var startTime = hrMinToInt(startHour.text.toString().toInt(), startMinute.text.toString().toInt(), startAmPm.isChecked)
            if (startTime < 0 || startTime >= 24*60) {
                AlertDialog.Builder(context).apply {
                    setTitle("Error")
                    setMessage("Start time entered incorrectly.")
                    setCancelable(false)
                    setPositiveButton("Ok") {dialog, id ->
                        Log.d(TAG,"Invalid entry")
                    }
                    show()
                }
                return
            }
            val length = courseLength.text.toString().toFloatOrNull()
            if (length == null || length < 0) {
                AlertDialog.Builder(context).apply {
                    setTitle("Error")
                    setMessage("Course length entered incorrectly.")
                    setCancelable(false)
                    setPositiveButton("Ok") {dialog, id ->
                        Log.d(TAG,"Invalid entry")
                    }
                    show()
                }
                return
            }
            val theColor: Int = courseColor!!.let { it }
            course?.let {
                it.name = courseNameText.text.toString()
                it.departmentCode =  departmentCode.text.toString()
                it.numberCode =  numberCode.text.toString()
                it.startTime = startTime
                it.courseTime = length
                it.isOnM = mondayBtn.isChecked
                it.isOnT = tuesdayBtn.isChecked
                it.isOnW = wednesdayBtn.isChecked
                it.isOnR = thursdayBtn.isChecked
                it.isOnF = fridayBtn.isChecked
                it.color = theColor
                courseViewModel.updateCourse(it)
            }

            activity?.onBackPressed()
        }
        catch (e: NumberFormatException) {
            AlertDialog.Builder(context).apply {
                setTitle("Error")
                setMessage("One of the fields was not entered properly")
                setCancelable(false)
                setPositiveButton("Ok") {dialog, id ->
                    Log.d(TAG,"Invalid entry")
                }
                show()
            }
        }
    }

    private fun hrMinToInt(hour: Int, min: Int, pm: Boolean): Int {
        return if (pm && hour != 12) {
            hour * 60 + min + 12 * 60
        }
        else {
            hour * 60 + min
        }
    }

    private fun intToHrMin(time: Int): IntArray {
        val result = intArrayOf(0,0,0)
        if (time >= 12*60) {
            result[2] = 1 // PM
        }
        if (time >= 13*60) {
            result[0] = (time - 12*60) / 60
            result[1] = ((((time - 12*60) / 60f) - result[0])*60).toInt()
        }
        else {
            result[0] = time / 60
            result[1] = (((time / 60f) - result[0])*60).toInt()
        }
        return result
    }

    private fun fieldsFilled(): Boolean {
        Log.d("Checking fields", "${courseNameText.text}, ${departmentCode.text}, ${numberCode.text}, ${startHour.text}, ${startMinute.text}, ${courseLength.text}}")
        return (courseNameText.text.isNotEmpty() &&
                departmentCode.text.isNotEmpty() &&
                numberCode.text.isNotEmpty() &&
                startHour.text.isNotEmpty() &&
                startMinute.text.isNotEmpty() &&
                courseLength.text.isNotEmpty() &&
                courseColor != null)
    }

    private fun closeKeyboard() {
        activity?.let {
            val view = it.currentFocus
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.currentFocus?.windowToken, 0)
        }
    }

    companion object {
        fun newInstance(courseId: UUID, isNew: Boolean): EditCourseFragment {
            val args = Bundle().apply {
                putSerializable(COURSE_ID_TAG, courseId)
                putSerializable(IS_NEW_TAG, isNew)
            }
            return EditCourseFragment().apply {
                arguments = args
            }
        }
    }
}