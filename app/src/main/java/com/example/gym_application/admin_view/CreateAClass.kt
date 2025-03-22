package com.example.gym_application.admin_view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.ClassModel
import com.example.gym_application.utils.ValidationClassCreation
import com.example.gym_application.utils.ValidationClassFields
import com.example.gym_application.utils.ValidationUtils
import com.example.gym_application.utils.utilsSetUpClassScheduleDate
import com.example.gym_application.utils.utilsSetUpEndTimeDropdown
import com.example.gym_application.utils.utilsSetUpSelectColorDropdown
import com.example.gym_application.utils.utilsSetUpSelectInstructorDropdown
import com.example.gym_application.utils.utilsSetUpSelectRoomDropdown
import com.example.gym_application.utils.utilsSetUpStartTimeDropdown
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateAClass : AppCompatActivity() {

    private lateinit var classTitle: EditText
    private lateinit var classDescription: EditText
    private lateinit var autoCompleteColorTextView: AutoCompleteTextView

    private lateinit var autoCompleteRoomTextView : AutoCompleteTextView
    private lateinit var autoCompleteInstructorTextView: AutoCompleteTextView
    private lateinit var classLimit : EditText
    private lateinit var classAvailabilityForRadioGroup : RadioGroup

    private lateinit var autoCompleteStartTime: AutoCompleteTextView
    private lateinit var autoCompleteEndTime : AutoCompleteTextView

    private lateinit var startDate: AutoCompleteTextView

    private var selectedColor: String = ""
    private var selectedRoom: String = ""
    private var selectedInstructor: String = ""

    private val database = FirebaseDatabase.getInstance().getReference("classes")
    private val instructorDatabase = FirebaseDatabase.getInstance().getReference("users")

    private val userFirebaseHelper = UserFirebaseDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_aclass)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        classTitle =  findViewById<EditText>(R.id.editTextClassTitle)
        classDescription = findViewById<EditText>(R.id.editTextClassDescription)

        classLimit = findViewById<EditText>(R.id.editTextClassLimit)
        classAvailabilityForRadioGroup = findViewById<RadioGroup>(R.id.radioGroup_ClassAvailabilityFor)
//        startDate = findViewById(R.id.auto_complete_starDate)

        setUpSelectColordropdown()
        setUpSelectRoomdropdown()
        setUpSelectInstructordropdown()

//        setUpStartTimeDropdown()
//        setUpEndTimeDropdown()
//        setUpStartDate()

    }

    fun addClassbtn(view: View) {
        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Class Creation failed!: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val title = classTitle.text.toString().trim()
        val description = classDescription.text.toString().trim()
        val capacity = classLimit.text.toString().trim().toIntOrNull() ?: 0

        val selectedGenderId = classAvailabilityForRadioGroup.checkedRadioButtonId
        val selectedGenderRadioButton = findViewById<RadioButton>(selectedGenderId)
        val genderRestriction = selectedGenderRadioButton.text.toString()

//        val startTime = autoCompleteStartTime.text.toString().trim()
//        val endTime = autoCompleteEndTime.text.toString().trim()
//
//        val startDate = startDate.text.toString().trim()

        val classId = database.push().key
        if (classId != null) {
            val newClass = ClassModel(classId,
                title, description, selectedColor,
                selectedRoom, selectedInstructor, capacity, 0, genderRestriction,
            )
            database.child(classId).setValue(newClass)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added Class Successfully", Toast.LENGTH_SHORT).show()
                    classTitle.text.clear()
                    classDescription.text.clear()
                    autoCompleteColorTextView.text.clear()
                    autoCompleteRoomTextView.text.clear()
                    autoCompleteInstructorTextView.text.clear()
                    classAvailabilityForRadioGroup.clearCheck()
                    finish()
//                    autoCompleteStartTime.text.clear()
//                    autoCompleteEndTime.text.clear()
//                   this.startDate.text.clear()

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error adding class: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        Toast.makeText(this,"Added Class Successfully",Toast.LENGTH_SHORT).show()

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpSelectColordropdown() {
        autoCompleteColorTextView = findViewById(R.id.auto_complete_txt)
        utilsSetUpSelectColorDropdown(this, autoCompleteColorTextView ) { color ->
            selectedColor = color
        }
    }

    private fun setUpSelectRoomdropdown() {
        autoCompleteRoomTextView = findViewById(R.id.auto_complete_room)
        utilsSetUpSelectRoomDropdown(this,autoCompleteRoomTextView) { room ->
            selectedRoom = room
        }

    }

    private fun setUpSelectInstructordropdown() {

        autoCompleteInstructorTextView = findViewById(R.id.auto_complete_instructor)
        userFirebaseHelper.fetchInstructors { instructorList ->
            utilsSetUpSelectInstructorDropdown(
                context = this, // Pass the context
                instructorList = instructorList,
                autoCompleteInstructorTextView = autoCompleteInstructorTextView,
                selectedInstructor = { selected ->
                    selectedInstructor = selected
                }
            )
        }
    }

    private fun setUpStartTimeDropdown() {
        autoCompleteStartTime = findViewById(R.id.auto_complete_startTime)
        utilsSetUpStartTimeDropdown(this, autoCompleteStartTime)
    }

    private fun setUpEndTimeDropdown() {
        autoCompleteEndTime = findViewById<AutoCompleteTextView>(R.id.auto_complete_endTime)
        utilsSetUpEndTimeDropdown(this,autoCompleteEndTime)
    }

    private fun setUpStartDate() {
        startDate = findViewById(R.id.auto_complete_starDate)
        utilsSetUpClassScheduleDate(this,startDate)
    }

    fun onCancelbtn(view: View){
        finish()
    }

    private fun validationFields() : String {

        return ValidationClassFields.validateClassFields(
            title = classTitle.text.toString().trim(),
            description = classDescription.text.toString().trim(),
            capacity = classLimit.text.toString().trim(),
            selectedGenderId = classAvailabilityForRadioGroup.checkedRadioButtonId,
            selectedColor = selectedColor,
            selectedRoom = selectedRoom,
            selectedInstructor = selectedInstructor
//                    startTime = autoCompleteStartTime.text.toString().trim(),
//            endTime = autoCompleteEndTime.text.toString().trim(),
//            startDate = startDate.text.toString().trim(),
        )
    }
}