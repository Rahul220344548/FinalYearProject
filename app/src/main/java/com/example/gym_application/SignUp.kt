package com.example.gym_application

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.model.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    //onClick register user
    fun registerUser(view: View) {
        if(genderRadioGroup.checkedRadioButtonId == -1)
        {
            Toast.makeText(this,  "please select gender", Toast.LENGTH_SHORT).show()
            return
        }else {
            Toast.makeText(this,  genderRadioGroup.id.toString() , Toast.LENGTH_SHORT).show()
            gender = findViewById<RadioButton>(genderRadioGroup.checkedRadioButtonId)
        }
        val validationMessage: String = validateDetails()

        //if it is not empty (there is an error)
        if(!validationMessage.isEmpty())
        {
            Toast.makeText(this, "Registration failed: ${validationMessage}", Toast.LENGTH_SHORT).show()
            return
        }

        dateOfBirth =  dayOfBirth.text.toString()+"/"+monthOfBirth.text.toString()+"/"+yearOfBirth.text.toString()

        addUser()
    }

    //validation for all the fields
    private fun validateDetails(): String {
        if(firstName.text.toString().isNullOrEmpty())
        {
            return "Please fill in your first name"
        }

        if(lastName.text.toString().isNullOrEmpty())
        {
            return "Please fill in your last name"
        }

        if(dayOfBirth.text.toString() != confirmPass.text.toString())
        {
            return "Please fill in your Day of birth"
        }

        if(monthOfBirth.text.toString() != confirmPass.text.toString())
        {
            return "Please fill in your month of birth"
        }

        if(yearOfBirth.text.toString() != confirmPass.text.toString())
        {
            return "Please fill in your year of birth"
        }

        //null
        if(dateOfBirth.isNullOrEmpty())
        {
            return "Please fill in your first name"
        }


        //if it is future date - send message
        if(dateOfBirth.isNullOrEmpty())
        {
            return "Please fill in your first name"
        }

        if(phoneNumber.text.toString().isNullOrEmpty())
        {
            return "Please fill in your Phone Number"
        }

        if(email.text.toString().isNullOrEmpty())
        {
            return "Please fill in your Email"
        }

        if(passsword.text.toString().isNullOrEmpty())
        {
            return "Please fill in your Password"
        }

        if(confirmPass.text.toString().isNullOrEmpty())
        {
            return "Please fill in your last name"
        }

        if(passsword.text.toString() != confirmPass.text.toString())
        {
            return "Password does not match"
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
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}