package com.example.gym_application.admin_view

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
import com.example.gym_application.model.ClassModel
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
    private lateinit var genderRestrictionsRadioGroup : RadioGroup

    private lateinit var autoCompleteStartTime: AutoCompleteTextView
    private lateinit var autoCompleteEndTime : AutoCompleteTextView

    private lateinit var autoCompleteStartClassAvalibility: AutoCompleteTextView

    private lateinit var selectedColor: String
    private lateinit var selectedRoom: String
    private lateinit var selectedInstructor: String

    private val database = FirebaseDatabase.getInstance().getReference("classes")
    private val instructorDatabase = FirebaseDatabase.getInstance().getReference("users")

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
        autoCompleteRoomTextView = findViewById(R.id.auto_complete_room)
        classLimit = findViewById<EditText>(R.id.editTextClassLimit)
        genderRestrictionsRadioGroup = findViewById<RadioGroup>(R.id.radioGroup_GenderRestrictions)

        autoCompleteStartClassAvalibility = findViewById(R.id.auto_complete_startClassAvaliablity)

        setUpSelectColordropdown()
        setUpSelectRoomdropdown()
        setUpSelectInstructordropdown()
        setUpStartTimeDropdown()
        setUpEndTimeDropdown()
        setUpStartClassAvaliablity()


    }

    fun addClassbtn(view: View) {
        val title = classTitle.text.toString().trim()
        val description = classDescription.text.toString().trim()
        val capacity = classLimit.text.toString().trim().toIntOrNull() ?: 0

        val selectedGenderId = genderRestrictionsRadioGroup.checkedRadioButtonId
        val selectedGenderRadioButton = findViewById<RadioButton>(selectedGenderId)
        val genderRestriction = selectedGenderRadioButton.text.toString()


        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this,"Please fill in blacks",Toast.LENGTH_SHORT).show()
            return
        }

        val classId = database.push().key
        if (classId != null) {
            val newClass = ClassModel(
                title,
                description,
                selectedColor,
                selectedRoom,
                selectedInstructor,
                capacity,
                genderRestriction
            )

            database.child(classId).setValue(newClass)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added Class Successfully", Toast.LENGTH_SHORT).show()
                    classTitle.text.clear()
                    classDescription.text.clear()
                    autoCompleteColorTextView.text.clear()
                    autoCompleteRoomTextView.text.clear()
                    autoCompleteInstructorTextView.text.clear()
                    genderRestrictionsRadioGroup.clearCheck()
                    finish()
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
        val colors = resources.getStringArray(R.array.colors)

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, colors)
        autoCompleteColorTextView.setAdapter(arrayAdapter)

        autoCompleteColorTextView.setOnItemClickListener { parent, _, position, _ ->
            selectedColor = parent.getItemAtPosition(position) as String
        }

    }

    private fun setUpSelectRoomdropdown() {

        val rooms = resources.getStringArray(R.array.rooms)

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rooms)
        autoCompleteRoomTextView.setAdapter(arrayAdapter)

        autoCompleteRoomTextView.setOnItemClickListener { parent, _, position, _ ->
            selectedRoom = parent.getItemAtPosition(position) as String
        }

    }

    private fun setUpSelectInstructordropdown() {

        autoCompleteInstructorTextView = findViewById(R.id.auto_complete_instructor)

        val instructorList = mutableListOf<String>()

        instructorDatabase.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    val role = userSnapshot.child("role").getValue(String::class.java)
                    if (role == "Instructor") {
                        val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                        val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                        val fullName = "$firstName $lastName"
                        instructorList.add(fullName)
                    }
                }
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, instructorList)
                autoCompleteInstructorTextView.setAdapter(arrayAdapter)

                autoCompleteInstructorTextView.setOnItemClickListener { parent, _, position, _ ->
                    selectedInstructor = parent.getItemAtPosition(position) as String
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching instructors: ${it.message}", Toast.LENGTH_SHORT).show()
        }



    }

    private fun setUpStartTimeDropdown() {

        autoCompleteStartTime = findViewById(R.id.auto_complete_startTime)

        val timeSlots = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        while (calendar.get(Calendar.HOUR_OF_DAY) < 20 ||
            (calendar.get(Calendar.HOUR_OF_DAY) == 20 && calendar.get(Calendar.MINUTE) == 30)) {
            timeSlots.add(timeFormat.format(calendar.time))
            calendar.add(Calendar.MINUTE, 30)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, timeSlots)
        autoCompleteStartTime.setAdapter(adapter)

        autoCompleteStartTime.setOnItemClickListener { parent, _, position, _ ->
            autoCompleteStartTime.setText(parent.getItemAtPosition(position) as String, false)
        }


    }

    private fun setUpEndTimeDropdown() {
        val autoCompleteEndTime = findViewById<AutoCompleteTextView>(R.id.auto_complete_endTime)

        val timeSlots = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 30)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        while (calendar.get(Calendar.HOUR_OF_DAY) < 20 ||
            (calendar.get(Calendar.HOUR_OF_DAY) == 20 && calendar.get(Calendar.MINUTE) == 0)) {
            timeSlots.add(timeFormat.format(calendar.time))
            calendar.add(Calendar.MINUTE, 30)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, timeSlots)
        autoCompleteEndTime.setAdapter(adapter)

        autoCompleteEndTime.setOnItemClickListener { parent, _, position, _ ->
            autoCompleteEndTime.setText(parent.getItemAtPosition(position) as String, false)
        }

    }

    private fun setUpStartClassAvaliablity() {

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate = dateFormat.format(Calendar.getInstance().time)

        autoCompleteStartClassAvalibility.setText(todayDate)
        autoCompleteStartClassAvalibility.isEnabled = false

    }

    fun onCancelbtn(view: View){
        finish()
    }

}