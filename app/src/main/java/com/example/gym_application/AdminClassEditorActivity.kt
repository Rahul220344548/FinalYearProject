package com.example.gym_application

import FirebaseDatabaseHelper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.utils.utilsSetUpClassScheduleDate
import com.example.gym_application.utils.utilsSetUpEndTimeDropdown
import com.example.gym_application.utils.utilsSetUpSelectColorDropdown
import com.example.gym_application.utils.utilsSetUpSelectInstructorDropdown
import com.example.gym_application.utils.utilsSetUpSelectRoomDropdown
import com.example.gym_application.utils.utilsSetUpStartTimeDropdown
import com.google.android.material.button.MaterialButton


class AdminClassEditorActivity : AppCompatActivity() {

    private lateinit var txtClassTitle : TextView
    private lateinit var txtClassDescription : TextView

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

    private lateinit var classId: String

    private val firebaseHelper = FirebaseDatabaseHelper()
    private val userFirebaseHelper = UserFirebaseDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_class_editor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        classId = intent.getStringExtra("classId")?:""

        txtClassTitle = findViewById<EditText>(R.id.editTextClassTitle)
        txtClassDescription = findViewById<EditText>(R.id.editTextClassDescription)
        classLimit = findViewById<EditText>(R.id.editTextClassLimit)

        initializeTextFields()


    }

    private fun initializeTextFields() {

        firebaseHelper.getClassInfobyId(classId) { classModel ->

            if (classModel != null) {

                txtClassTitle.setText(classModel.classTitle)
                txtClassDescription.setText(classModel.classDescription)

                setUpSelectColordropdown()
                autoCompleteColorTextView.setText(classModel.classColor,false)

                setUpSelectRoomdropdown()
                autoCompleteRoomTextView.setText(classModel.classLocation, false)

                setUpSelectInstructordropdown()
                autoCompleteInstructorTextView.setText(classModel.classInstructor,false)


                classLimit.setText(classModel.classMaxCapacity.toString())

                setUpClassAvailabilityFor(classModel.classAvailabilityFor)

                setUpStartTimedropdown()
                autoCompleteStartTime.setText(classModel.classStartTime,false)

                setUpEndTimedropdown()
                autoCompleteEndTime.setText(classModel.classEndTime,false)

            }else{
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateClassBtn( view: View) {

    }

    fun onDeleteBtn (view: View) {
        showDeleteClassDialog()
    }

    fun onCancelbtn(view : View) {
        finish()
    }

    private fun showDeleteClassDialog() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.delete_class_dialog_box, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.cancelbtn)
        val btnConfirmDeletion = dialogView.findViewById<MaterialButton>(R.id.btnConfirmDelete)

        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirmDeletion.setOnClickListener {
            alertDialog.dismiss()
            deleteClassFromDatabase()
        }

    }

    private fun deleteClassFromDatabase() {

    }

    private fun setUpSelectColordropdown() {
        autoCompleteColorTextView = findViewById(R.id.auto_complete_txt)
        utilsSetUpSelectColorDropdown(this, autoCompleteColorTextView ) { color ->
            selectedColor = color
        }
    }

    private fun setUpSelectRoomdropdown() {
        autoCompleteRoomTextView = findViewById(R.id.auto_complete_room)
        utilsSetUpSelectRoomDropdown(this, autoCompleteRoomTextView) { room ->
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

    private fun setUpClassAvailabilityFor(classAvailabilityFor: String) {
        classAvailabilityForRadioGroup = findViewById<RadioGroup>(R.id.radioGroup_ClassAvailabilityFor)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup_ClassAvailabilityFor)
        val radioGenderAll = findViewById<RadioButton>(R.id.radio_genderAll)
        val radioGenderMale = findViewById<RadioButton>(R.id.radio_genderMale)
        val radioGenderFemale = findViewById<RadioButton>(R.id.radio_genderFemale)

        when (classAvailabilityFor.lowercase()) {
            "all" -> radioGenderAll.isChecked = true
            "male" -> radioGenderMale.isChecked = true
            "female" -> radioGenderFemale.isChecked = true
        }

    }

    private fun setUpStartTimedropdown() {
        autoCompleteStartTime = findViewById(R.id.auto_complete_startTime)
        utilsSetUpStartTimeDropdown(this, autoCompleteStartTime)
    }

    private fun setUpEndTimedropdown() {
        autoCompleteEndTime = findViewById<AutoCompleteTextView>(R.id.auto_complete_endTime)
        utilsSetUpEndTimeDropdown(this,autoCompleteEndTime)
    }

    private fun setUpClassScheduledropdown() {
        startDate = findViewById(R.id.auto_complete_starDate)
        utilsSetUpClassScheduleDate(this,startDate)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}