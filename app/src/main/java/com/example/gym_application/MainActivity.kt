package com.example.gym_application

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.model.User
import com.example.gym_application.model.UserPersonalDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.buttonRegister)
        loginButton = findViewById(R.id.buttonLogin)

        registerButton.setOnClickListener { registerUser() }
        loginButton.setOnClickListener { loginUser() }
    }
    //function to register users
     fun registerUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserData(userId, email, "test111")
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**@param email: this is string
     * @param name: this is name string
     * @return saves data to the database table
     * */
    private fun saveUserData(userId: String, email: String, name: String) {
        val user = User(userId, email)

        val userPersonalDetails = UserPersonalDetails(name)

        addUserData(userId, user);
        addUserDetailData(userId, userPersonalDetails);

    }

    private fun addUserData(userId: String, user: User): String {
        database.reference.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        val test: DatabaseReference = database.getReference("users").child(userId)
        Toast.makeText(this,test.toString(),Toast.LENGTH_SHORT).show()
        return "test"
    }

    private fun addUserDetailData(userId: String, userPersonalDetails: UserPersonalDetails){
        database.reference.child("userPersonalDetails").child(userId).setValue(userPersonalDetails)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Login user
    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to another activity (e.g., Dashboard) 
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


}

//    fun registerUser(view: View) {
//        Toast.makeText(this@MainActivity, emailEditText.text.toString(), Toast.LENGTH_SHORT).show()
//    }
//
//    fun loginUser(view: View) {
//        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
//    }
