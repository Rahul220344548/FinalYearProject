package com.example.gym_application

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AccountFragment : Fragment() {

    private lateinit var txtFullName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        // Find Views
        txtFullName = view.findViewById(R.id.EditFirstNameAndLastName)
        txtEmail = view.findViewById(R.id.EditUserEmailAddress)

        // Fetch and set profile details
        setProfileDetails()

        val btnEditProfile: AppCompatButton = view.findViewById(R.id.btnEditProfile)

        btnEditProfile.setOnClickListener {
            goToEditProfile()
        }

        return view
    }

    private fun goToEditProfile() {
        val intent = Intent(requireContext(), EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun setProfileDetails() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = database.child(userId)

            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val firstName = snapshot.child("firstName").value.toString()
                    val lastName = snapshot.child("lastName").value.toString()

                    // Set full name
                    txtFullName.text = "$firstName $lastName"
                } else {
                    txtFullName.text = "User Not Found"
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

            // Fetch email from FirebaseAuth (not Realtime Database)
            val userEmail = auth.currentUser?.email
            if (userEmail != null) {
                txtEmail.text = userEmail
            } else {
                txtEmail.text = "Email Not Available"
            }
        } else {
            txtFullName.text = "Not Logged In"
            txtEmail.text = "Not Logged In"
        }
    }
}
