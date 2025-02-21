package com.example.gym_application.admin_view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
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
import com.example.gym_application.utils.ValidationClassCreation
import com.example.gym_application.utils.ValidationUtils
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
    private lateinit var autoCompleteEndClassAvalibility: AutoCompleteTextView

    private var selectedColor: String = ""
    private var selectedRoom: String = ""
    private var selectedInstructor: String = ""

    private lateinit var checkBoxes: List<CheckBox>
    private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

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
        autoCompleteEndClassAvalibility = findViewById(R.id.auto_complete_endClassAvaliablity)

        initializeCheckBoxes()

        setUpSelectColordropdown()
        setUpSelectRoomdropdown()
        setUpSelectInstructordropdown()
        setUpStartTimeDropdown()
        setUpEndTimeDropdown()
        setUpStartClassAvailability()
        setUpEndClassAvailability()

    }

    fun addClassbtn(view: View) {

        val validationMessage : String = validationFields()
        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Class Creation failed!: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val title = classTitle.text.toString().trim()
        val description = classDescription.text.toString().trim()
        val capacity = classLimit.text.toString().trim().toIntOrNull() ?: 0

        val selectedGenderId = genderRestrictionsRadioGroup.checkedRadioButtonId
        val selectedGenderRadioButton = findViewById<RadioButton>(selectedGenderId)
        val genderRestriction = selectedGenderRadioButton.text.toString()

        val startTime = autoCompleteStartTime.text.toString().trim()
        val endTime = autoCompleteEndTime.text.toString().trim()

        val startAvailability = autoCompleteStartClassAvalibility.text.toString().trim()
        val endAvailability = autoCompleteEndClassAvalibility.text.toString().trim()

        val occurrences = getSelectedOccurrences()


        val classId = database.push().key
        if (classId != null) {
            val newClass = ClassModel(
                title, description, selectedColor,
                selectedRoom, selectedInstructor, capacity, genderRestriction,
                startTime, endTime,
                startAvailability, endAvailability,
                occurrences
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
                    autoCompleteStartTime.text.clear()
                    autoCompleteEndTime.text.clear()
                    autoCompleteStartClassAvalibility.text.clear()
                    autoCompleteEndClassAvalibility.text.clear()
                    resetCheckBoxes()
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
        autoCompleteEndTime = findViewById<AutoCompleteTextView>(R.id.auto_complete_endTime)

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

    private fun setUpStartClassAvailability() {

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate = dateFormat.format(Calendar.getInstance().time)

        autoCompleteStartClassAvalibility.setText(todayDate)
        autoCompleteStartClassAvalibility.isEnabled = false

    }

    private fun setUpEndClassAvailability() {
        autoCompleteEndClassAvalibility.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                    if (selectedCalendar.before(calendar)) {
                        Toast.makeText(this, "End Date cannot be in the past!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val selectedDate = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear
                        )
                        autoCompleteEndClassAvalibility.setText(selectedDate)
                    }
                }, year, month, day)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()

        }
    }

    private fun initializeCheckBoxes() {
        checkBoxes = listOf(
            findViewById(R.id.checkbox_mon),
            findViewById(R.id.checkbox_tue),
            findViewById(R.id.checkbox_wed),
            findViewById(R.id.checkbox_thu),
            findViewById(R.id.checkbox_fri),
            findViewById(R.id.checkbox_sat),
            findViewById(R.id.checkbox_sun)
        )
    }

    private fun getSelectedOccurrences(): List<String> {
        return checkBoxes.mapIndexedNotNull { index, checkBox ->
            if (checkBox.isChecked) daysOfWeek[index] else null
        }
    }
    private fun resetCheckBoxes() {
        checkBoxes.forEach { it.isChecked = false }
    }
    fun onCancelbtn(view: View){
        finish()
    }

    private fun validationFields() : String {

        val title = classTitle.text.toString().trim()
        val description = classDescription.text.toString().trim()
        val capacity = classLimit.text.toString().trim()
        val selectedGenderId = genderRestrictionsRadioGroup.checkedRadioButtonId
        val startTime = autoCompleteStartTime.text.toString().trim()
        val endTime = autoCompleteEndTime.text.toString().trim()
        val startAvailability = autoCompleteStartClassAvalibility.text.toString().trim()
        val endAvailability = autoCompleteEndClassAvalibility.text.toString().trim()
        val occurrences = getSelectedOccurrences()

        if (!ValidationClassCreation.isValidClassTitle(title)) {
            return "Please enter a class Title (at least 3 characters)"
        }

        if (!ValidationClassCreation.isValidClassDescription(description)) {
            return "Please enter a class Description"
        }

        if (!ValidationClassCreation.isValidClassColor(selectedColor)) {
            return "Please select a class color"
        }

        if (!ValidationClassCreation.isValidSelectRoom(selectedRoom)) {
            return "Please select a Room"
        }

        if (!ValidationClassCreation.isValidSelectInstructor(selectedInstructor)) {
            return "Please select a Instructor"
        }

        if (!ValidationClassCreation.isValidCapacity(capacity)) {
            return "Please enter a valid class limit ( must be < 20 )"
        }

        if (!ValidationClassCreation.isValidGenderSelection(selectedGenderId)) {
            return "Please select a gender restriction"
        }

        if (!ValidationClassCreation.isValidTime(startTime, endTime)) {
            return "Invalid time: Class duration must be 30 minutes or 1 hour"
        }

        if (!ValidationClassCreation.isValidEndDate(startAvailability, endAvailability)) {
            return "End date must be after or on the start date"
        }

        if (!ValidationClassCreation.isValidOccurrences(occurrences)) {
            return "Please select at least one occurrence"
        }

        return ""
    }
}