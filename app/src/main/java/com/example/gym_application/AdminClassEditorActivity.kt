package com.example.gym_application

import FirebaseDatabaseHelper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.adapter.ScheduleFirebaseHelper
import com.example.gym_application.utils.ClassBookingUtils
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.ValidationClassCreationFields
import com.google.android.material.button.MaterialButton



class AdminClassEditorActivity : AppCompatActivity() {

    private lateinit var txtClassTitle : TextView
    private lateinit var txtClassDescription : TextView

    private lateinit var autoCompleteColorTextView: AutoCompleteTextView
    private lateinit var classLimit : EditText
    private lateinit var autoCompleteclassAvailabilityFor : AutoCompleteTextView

    private lateinit var inClassTitle: String
    private lateinit var inClassDescription : String
    private lateinit var inColorTextView : String
    private lateinit var inClassAvailabilityFor : String


    private var selectedColor: String = ""
    private var selectedAvailability : String = ""
    private lateinit var classId: String

    private val firebaseHelper = FirebaseDatabaseHelper()
    private val firebaseScheduleHelper = ScheduleFirebaseHelper()

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
        classLimit = findViewById<EditText>(R.id.class_template_limit)
        autoCompleteclassAvailabilityFor = findViewById(R.id.auto_complete_template_availability_for)

        initializeTextFields()

    }

    private fun initializeTextFields() {

        autoCompleteColorTextView = findViewById(R.id.auto_complete_txt)
        autoCompleteclassAvailabilityFor =
            findViewById(R.id.auto_complete_template_availability_for)


        inColorTextView = intent.getStringExtra("classColor") ?: ""
        autoCompleteColorTextView.setText(inColorTextView)
        ClassBookingUtils.setUpSelectClassColordropdown(this, autoCompleteColorTextView) { color ->
            selectedColor = color
        }

        inClassAvailabilityFor = intent.getStringExtra("classAvailabilityFor") ?: ""
        autoCompleteclassAvailabilityFor.setText(inClassAvailabilityFor)
        ClassBookingUtils.setUpSelectAvailabilityFordropdown(
            this,
            autoCompleteclassAvailabilityFor
        ) { availability ->
            selectedAvailability = availability
        }

        inClassTitle = intent.getStringExtra("classTitle") ?: ""
        txtClassTitle.setText(inClassTitle)

        inClassDescription = intent.getStringExtra("classDescription") ?: ""
        txtClassDescription.setText(inClassDescription)


        val inClassLimit = intent.getIntExtra("classMaxCapacity", 0)
        classLimit.setText(inClassLimit.toString())


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onUpdateBtn(view: View) {

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Class Update Failed: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }
        val title = txtClassTitle.text.toString().trim()
        val description = txtClassDescription.text.toString().trim()
        val color = autoCompleteColorTextView.text.toString().trim()
        val capacity = classLimit.text.toString().toIntOrNull() ?: 0
        val classAvailableFor = autoCompleteclassAvailabilityFor.text.toString().trim()

        val classUpdate = mapOf (
            "classTitle" to title,
            "classDescription" to description,
            "classColor" to color,
            "classMaxCapacity" to capacity,
            "classAvailabilityFor" to classAvailableFor,
        )

        firebaseHelper.updateClassDetails(classId, classUpdate) { success ->
            if (success) {
                val scheduleUpdate = mapOf<String,Any> (
                    "classTitle" to title,
                    "classDescription" to description,
                    "classColor" to color,
                    "classMaxCapacity" to capacity,
                    "classAvailabilityFor" to classAvailableFor,
                )
                firebaseScheduleHelper.updateScheduleDetails(classId, scheduleUpdate) { success ->
                    if (success) {
                        DialogUtils.showToast(this, "Schedules updated successfully")
                        finish()

                    } else {
                        DialogUtils.showToast(this, "There are no active schedules to update!")
                    }
                }
            } else {
                DialogUtils.showToast(this, "Failed to update class")
            }
        }


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
        firebaseHelper.deleteClass( classId, onSuccess = {
            Toast.makeText(this, "Class deleted successfully", Toast.LENGTH_SHORT).show()
            finish()
        },
            onFailure = { e ->
                Toast.makeText(this, "Failed to delete class: ${e.message}", Toast.LENGTH_LONG).show()
            })
    }

    private fun validationFields() : String {

        return ValidationClassCreationFields.validationClassCreationFields(
            title = txtClassTitle.text.toString().trim(),
            description = txtClassDescription.text.toString().trim(),
            selectedColor = autoCompleteColorTextView.text.toString().trim(),
            capacity = classLimit.text.toString().trim(),
            availabilityFor = autoCompleteclassAvailabilityFor.text.toString().trim()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}