package com.example.gym_application.admin_view

import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.UserDetails
import com.example.gym_application.utils.ValidationStaffFields
import com.example.gym_application.utils.utilsSetUpSelectUserGender
import com.example.gym_application.utils.utilsSetUpSelectUserRoles
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminAddStaff : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var dayOfBirth: EditText
    private lateinit var monthOfBirth: EditText
    private lateinit var yearOfBirth: EditText

    private lateinit var autoCompleteGenderTextView: AutoCompleteTextView
    private lateinit var autoCompleteRoleTextView : AutoCompleteTextView

    private lateinit var dateOfBirth: String
    private lateinit var phoneNumber: EditText
    private lateinit var email: EditText
    private lateinit var passsword: EditText
    private lateinit var reEnterPassword: EditText
    private lateinit var signInTextView: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    private var selectedGender: String = ""
    private var selectedRoles: String = ""

    private val firebaseHelper = UserFirebaseDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_add_staff)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"
        setUpSelectGenderdropdown()
        setUpSelectRoledropdown()
        firstName = findViewById<EditText>(R.id.editStaffTextFirstName)
        lastName = findViewById<EditText>(R.id.editStaffTextLastName)


        dayOfBirth = findViewById<EditText>(R.id.editStaffDay)
        monthOfBirth = findViewById<EditText>(R.id.editStaffMonth)
        yearOfBirth = findViewById<EditText>(R.id.editStaffYear)
        phoneNumber = findViewById<EditText>(R.id.editStaffTextPhoneNumber)
        email = findViewById<EditText>(R.id.editStaffTextEmail)
        passsword = findViewById<EditText>(R.id.editStaffTextPassword)
        reEnterPassword = findViewById<EditText>(R.id.editStaffTextReEnterPassword)

        auth = FirebaseAuth.getInstance()
    }

    fun addStaffToAuthDataBase(view : View) {

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Staff Registration failed: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        dateOfBirth = String.format(
            "%s/%s/%s",
            dayOfBirth.text.toString().trim(),
            monthOfBirth.text.toString().trim(),
            yearOfBirth.text.toString().trim()
        )

        val email = email.text.trim().toString()
        val password = passsword.text.trim().toString()
        firebaseHelper.createAccountInAuthentication(email,password) { isSuccess, userIdOrError ->
            if (isSuccess) {
                if (userIdOrError != null) {
                    saveStaffDetails(userIdOrError)
                    finish()
                }
            }else {
                showToast("Regstritation Failed!")
            }
        }
    }

    private fun saveStaffDetails(userId: String) {
        val staff = UserDetails(
            firstName.text.trim().toString(),
            lastName.text.trim().toString(),
            dateOfBirth,
            autoCompleteGenderTextView.text.toString().trim(),
            phoneNumber.text.toString(),
            autoCompleteRoleTextView.text.toString().trim(),
            status = "active")
        addStaffDataToDatabase(userId,staff)
    }

    private fun addStaffDataToDatabase(userId: String, userDetails: UserDetails) {
        firebaseHelper.addAccountInUserDatabase( userId, userDetails) { isSuccess, errorMessage ->
            if (isSuccess) {
                showToast("User registered successfully!")
            }else {
                showToast("Failed to save user data: $errorMessage")
            }
        }
    }

    private fun setUpSelectGenderdropdown() {
        autoCompleteGenderTextView = findViewById(R.id.select_staff_gender)
        utilsSetUpSelectUserGender(this,autoCompleteGenderTextView) { gender ->
            selectedGender = gender
        }
    }

    private fun setUpSelectRoledropdown() {
        autoCompleteRoleTextView = findViewById(R.id. select_staff_role)
        utilsSetUpSelectUserRoles( this, autoCompleteRoleTextView) { role ->
            selectedRoles = role
        }
    }


    private fun validationFields(): String{
        return ValidationStaffFields.validateStaffFields(
            staffFirstName = firstName.text.toString(),
            staffLastName = lastName.text.toString(),
            staffDOBSelectedGender = autoCompleteGenderTextView.text.toString().trim(),
            staffSelectedRole = autoCompleteRoleTextView.text.toString().trim(),
            staffDOBDay = dayOfBirth.text.toString(),
            staffDOBMonth = monthOfBirth.text.toString(),
            staffDOBYear = yearOfBirth.text.toString(),
            staffPhoneNumber = phoneNumber.text.toString(),
            staffEmailAddress = email.text.toString(),
            staffPassword = passsword.text.toString(),
            staffReEnterPassword = reEnterPassword.text.toString(),
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}