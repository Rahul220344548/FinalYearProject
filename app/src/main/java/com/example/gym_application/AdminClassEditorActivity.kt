package com.example.gym_application

import FirebaseDatabaseHelper
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.utils.utilsSetUpSelectColorDropdown
import com.example.gym_application.utils.utilsSetUpSelectInstructorDropdown
import com.example.gym_application.utils.utilsSetUpSelectRoomDropdown


class AdminClassEditorActivity : AppCompatActivity() {

    private lateinit var txtClassTitle : TextView
    private lateinit var txtClassDescription : TextView

    private lateinit var autoCompleteColorTextView: AutoCompleteTextView
    private lateinit var autoCompleteRoomTextView : AutoCompleteTextView
    private lateinit var autoCompleteInstructorTextView: AutoCompleteTextView


    private lateinit var txtClassScheduledDate : TextView
    private lateinit var txtClassScheduledTime : TextView
    private lateinit var txtclassLength : TextView
    private lateinit var txtclassAvailability: TextView
    private lateinit var txtClassRemainingSpot: TextView
    private lateinit var txtClassLocation : TextView
    private lateinit var txtClassInstructor : TextView

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


            }else{
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
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


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}