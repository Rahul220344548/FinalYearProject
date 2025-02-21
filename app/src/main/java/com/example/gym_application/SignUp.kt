package com.example.gym_application

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.model.UserDetails
import com.example.gym_application.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
// merged membership branch
class SignUp : AppCompatActivity() {
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var dayOfBirth: EditText
    private lateinit var monthOfBirth: EditText
    private lateinit var yearOfBirth: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var gender: RadioButton
    private lateinit var dateOfBirth: String
    private lateinit var phoneNumber: EditText
    private lateinit var email: EditText
    private lateinit var passsword: EditText
    private lateinit var confirmPass: EditText
    private lateinit var signInTextView: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firstName = findViewById<EditText>(R.id.editTextFirstName)
        lastName = findViewById<EditText>(R.id.editTextLastName)
        genderRadioGroup = findViewById<RadioGroup>(R.id.radioGroup_Gender)
        dayOfBirth = findViewById<EditText>(R.id.editDay)
        monthOfBirth = findViewById<EditText>(R.id.editMonth)
        yearOfBirth = findViewById<EditText>(R.id.editYear)
        phoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        email = findViewById<EditText>(R.id.editTextEmail)
        passsword = findViewById<EditText>(R.id.editTextPassword)
        confirmPass = findViewById<EditText>(R.id.editTextReEnterPassword)

        signInTextView= findViewById(R.id.textViewSignIn)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        signInTextView.paintFlags = signInTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        signInTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //onClick register user
    fun registerUser(view: View) {

        if (genderRadioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            return
        }

        gender = findViewById(genderRadioGroup.checkedRadioButtonId)


        val validationMessage: String = validateDetails()
        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Registration failed: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        dateOfBirth = String.format(
            "%s/%s/%s",
            dayOfBirth.text.toString().trim(),
            monthOfBirth.text.toString().trim(),
            yearOfBirth.text.toString().trim()
        )
        addUser()
    }

    //validation for all the fields
    private fun validateDetails(): String {

        if (!ValidationUtils.isValidFirstname(firstName.text.toString())) {
            return "Please enter a valid first name (at least 2 characters)"
        }

        if (!ValidationUtils.isValidLastName(lastName.text.toString())) {
            return "Please enter a valid last name (at least 2 characters)"
        }

        if (!ValidationUtils.isValidDay(dayOfBirth.text.toString())) {
            return "Please enter a valid day of birth (1-31)"
        }

        if (!ValidationUtils.isValidMonth(monthOfBirth.text.toString())) {
            return "Please enter a valid month of birth (1-12)"
        }

        if (!ValidationUtils.isValidYear(yearOfBirth.text.toString())) {
            return "Please enter a valid year of birth"
        }

        if (!ValidationUtils.isValidDateOfBirth(dayOfBirth.text.toString(), monthOfBirth.text.toString(), yearOfBirth.text.toString())) {
            return "Invalid date of birth. Ensure it's a past date."
        }

        if (!ValidationUtils.isValidPhoneNumber(phoneNumber.text.toString())) {
            return "Please enter a valid phone number (10-15 digits, can start with +)"
        }

        if (!ValidationUtils.isValidEmail(email.text.toString())) {
            return "Please enter a valid email address (e.g., user@example.com)"
        }

        if (!ValidationUtils.isValidPassword(passsword.text.toString())) {
            return "Password must be at least 6 characters long"
        }

        if (!ValidationUtils.doPasswordsMatch(passsword.text.toString(), confirmPass.text.toString())) {
            return "Passwords do not match"
        }

        if (!ValidationUtils.isValidGender(genderRadioGroup.checkedRadioButtonId)) {
            return "Please select a gender"
        }

        return ""


    }

    //saves user email and password to Auth in firebase
    private fun addUser() {
        if (!::auth.isInitialized) {
            Toast.makeText(this, "FirebaseAuth is not initialized yet.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email.text.trim().toString(), passsword.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserDetails(userId)
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //creates userDetails Object
    private fun saveUserDetails(userId: String) {
        val user = UserDetails(firstName.text.trim().toString(),
            lastName.text.trim().toString(),
            dateOfBirth,
            gender.text.toString(),
            phoneNumber.text.trim().toString(),
            "Member")

        addUserData(userId, user);

    }

    //save into database
    private fun addUserData(userId: String, userDetails: UserDetails) {
        database.reference.child("users").child(userId).setValue(userDetails)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to save user data: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace() // Print error log in Logcat
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}