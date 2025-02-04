package com.example.gym_application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var txtUserName: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        // Initialize UI Elements
        txtUserName = view.findViewById(R.id.txt_userName)

        setUserName()

        return view
    }

    private fun setUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.child(userId).child("firstName")

            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val firstName = snapshot.value.toString()
                    txtUserName.text = firstName
                } else {
                    txtUserName.text = "User Not Found"
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            txtUserName.text = "Not Logged In"
        }
    }
}
