package com.example.gym_application

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhoneNumber: EditText
    private lateinit var txtGender : EditText
    private lateinit var txtBirthday : EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.user_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        txtFirstName = findViewById<EditText>(R.id.editTextFirstName)
        txtLastName = findViewById<EditText>(R.id.editTextLastName)
        txtPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        txtGender = findViewById<EditText>(R.id.editTextGender)
        txtBirthday = findViewById<EditText>(R.id.editTextBirthday)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        initializeEditTextField()
    }

    private fun initializeEditTextField(){
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = database.child(userId)

            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val firstName = snapshot.child("firstName").value.toString()
                    val lastName = snapshot.child("lastName").value.toString()
                    val phoneNumber = snapshot.child("phoneNumber").value.toString()
                    val gender = snapshot.child("gender").value.toString()
                    val birthday = snapshot.child("dateOfBirth").value.toString()

                    txtFirstName.setText(firstName)
                    txtLastName.setText(lastName)
                    txtPhoneNumber.setText(phoneNumber)
                    txtGender.setText(gender)
                    txtBirthday.setText(birthday)

                    txtGender.isEnabled = false
                    txtBirthday.isEnabled = false

                }
            }.addOnFailureListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun onSave(view: View){
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = database.child(userId)

            val firstName = txtFirstName.text.toString().trim()
            val lastName = txtLastName.text.toString().trim()
            val phoneNumber = txtPhoneNumber.text.toString().trim()

            if (!isValidFirstname(firstName)) {
                Toast.makeText(this, "First name must be at least 2 characters.", Toast.LENGTH_SHORT).show()
                return
            }

            if (!isValidLastName(lastName)) {
                Toast.makeText(this, "Last name must be at least 2 characters.", Toast.LENGTH_SHORT).show()
                return
            }

            if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Invalid phone number. It should be between 10 to 15 digits.", Toast.LENGTH_SHORT).show()
                return
            }

            // Create a HashMap to update the database
            val userUpdates = mapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "phoneNumber" to phoneNumber
            )

            userRef.updateChildren(userUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun isValidFirstname( firstname : String) : Boolean {
        return firstname.isNotEmpty() && firstname.length >=2
    }

    fun isValidLastName ( lastname : String) : Boolean {
        return lastname.isNotEmpty() && lastname.length >=2
    }

    fun isValidPhoneNumber ( phoneNumber : String ) : Boolean {
        val phonePattern = "^[+]?[0-9]{10,15}$"
        return phoneNumber.matches(phonePattern.toRegex())
    }

    fun onCancel(view: View){
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}