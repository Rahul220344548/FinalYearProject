package com.example.gym_application.admin_view

import FirebaseDatabaseHelper
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.ClassModel
import com.example.gym_application.model.ClassTemplate
import com.example.gym_application.utils.ClassBookingUtils
import com.example.gym_application.utils.ValidationClassCreation
import com.example.gym_application.utils.ValidationClassCreationFields
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


    private lateinit var classLimit : EditText
    private lateinit var autoCompleteclassAvailabilityFor : AutoCompleteTextView


    private var selectedColor: String = ""

    private var selectedAvailability : String = ""

    private val database = FirebaseDatabase.getInstance().getReference("classes")
    private val classFirebaseHelper = FirebaseDatabaseHelper()

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
        autoCompleteColorTextView = findViewById(R.id.auto_complete_txt)
        autoCompleteclassAvailabilityFor = findViewById(R.id.auto_complete_template_availability_for)
        classLimit = findViewById<EditText>(R.id.class_template_limit)

        ClassBookingUtils.setUpSelectClassColordropdown(this, autoCompleteColorTextView) { color ->
            selectedColor = color
        }

        ClassBookingUtils.setUpSelectAvailabilityFordropdown(this, autoCompleteclassAvailabilityFor) { availability ->
            selectedAvailability = availability
        }



    }

    fun addClassbtn(view: View) {
        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Class Creation failed!: $validationMessage", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val title = classTitle.text.toString().trim()
        val description = classDescription.text.toString().trim()
        val color = autoCompleteColorTextView.text.toString().trim()
        val capacity = classLimit.text.toString().trim().toIntOrNull() ?: 0
        val availabilityFor = autoCompleteclassAvailabilityFor.text.toString().trim()

        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()

        val classId = database.push().key ?: return

        val newClassTemplate = ClassTemplate(
            classId,
            title,
            description,
            color,
            capacity,
            availabilityFor
        )

        classFirebaseHelper.createClassTemplate(
            classId,
            classTemplate = newClassTemplate,
            onSuccess = {
                Toast.makeText(this ,"Class created successfully!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = { exception ->
                Toast.makeText(this, "Failed to create class: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    fun onCancelbtn(view: View){
        finish()
    }

    private fun validationFields() : String {

        return ValidationClassCreationFields.validationClassCreationFields(
            title = classTitle.text.toString().trim(),
            description = classDescription.text.toString().trim(),
            selectedColor = selectedColor,
            capacity = classLimit.text.toString().trim(),
            availabilityFor = selectedAvailability,
        )
    }
}