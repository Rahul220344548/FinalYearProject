package com.example.gym_application

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.admin_view.AdminDashboardActivity
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var forgotPassword : TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    @SuppressLint("MissingInflatedId")
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
        forgotPassword = findViewById(R.id.idForgotPassword)


        val btnJohnAutoLogin: Button = findViewById(R.id.btnJohnAutoLogin)
        btnJohnAutoLogin.setOnClickListener {
            autoRahulLogin()
        }

        val btnRahulAutoLogin: Button = findViewById(R.id.btnRahulAutoLogin)
        btnRahulAutoLogin.setOnClickListener {
            autoJohnLogin()
        }

        val btnAdminAutoLogin : Button = findViewById(R.id.btnAdminAutoLogin)
        btnAdminAutoLogin.setOnClickListener {
            autoAdminLogin()
        }

        forgotPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val userEmail = view.findViewById<EditText>(R.id.editEmailBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()

        }


    }

    fun autoJohnLogin() {
        val testEmail = "test@gmail.com"
        val testPassword = "password123"

        auth.signInWithEmailAndPassword(testEmail, testPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Auto Login successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                    Toast.makeText(this, "Auto Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun autoRahulLogin() {
        val testEmail = "testname@gmail.com"
        val testPassword = "password1"

        auth.signInWithEmailAndPassword(testEmail, testPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Auto Login successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                    Toast.makeText(this, "Auto Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun autoAdminLogin() {
        val testEmail = "admin@admin.com"
        val testPassword = "admin123"

        auth.signInWithEmailAndPassword(testEmail, testPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Auto Login successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                    Toast.makeText(this, "Auto Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Login user
    fun loginUser(view: View) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    checkUserRoleAndStatus(userId)
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun checkUserRoleAndStatus( userId : String) {

        val firebaseHelper = UserFirebaseDatabaseHelper()

        firebaseHelper.fetchAuthentication(userId) { isActive, role ->
            if (!isActive) {
                Toast.makeText(this, "Your account has been deactivated. Contact support.", Toast.LENGTH_LONG).show()
            } else {

                when (role) {
                    "Admin" -> startActivity(Intent(this, AdminDashboardActivity::class.java))
                    "Instructor" -> startActivity(Intent(this, SignUp::class.java))
                    else -> startActivity(Intent(this, HomeActivity::class.java)) // Default for members
                }
                finish()
            }
        }


    }

    fun goToSignUpPage(view: View) {
        startActivity(Intent(this, SignUp::class.java))
    }

    private fun compareEmail( emailEditText: EditText ) {

        if (emailEditText.text.toString().isEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
            Toast.makeText(this, "Invalid Email address!", Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(emailEditText.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Check you Email!", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
