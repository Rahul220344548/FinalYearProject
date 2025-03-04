package com.example.gym_application

import FirebaseDatabaseHelper
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class AdminClassEditorActivity : AppCompatActivity() {

    private lateinit var txtClassTitle : TextView
    private lateinit var txtClassScheduledDate : TextView
    private lateinit var txtClassScheduledTime : TextView
    private lateinit var txtclassLength : TextView
    private lateinit var txtclassAvailability: TextView
    private lateinit var txtClassRemainingSpot: TextView
    private lateinit var txtClassLocation : TextView
    private lateinit var txtClassInstructor : TextView
    private lateinit var txtClassDescription : TextView

    private lateinit var classId: String


    private val firebaseHelper = FirebaseDatabaseHelper()

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
            }else{
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }





    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}