package com.example.gym_application

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.button.MaterialButton
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


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")


        txtFullName = view.findViewById(R.id.EditFirstNameAndLastName)
        txtEmail = view.findViewById(R.id.EditUserEmailAddress)


        setProfileDetails()

        val btnEditProfile: AppCompatButton = view.findViewById(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {
            goToEditProfile()
        }

        val btnMyBookings : AppCompatButton = view.findViewById(R.id.btnMyBookings)
        btnMyBookings.setOnClickListener {
            goToMyBookingsPage()
        }

        val btnMembership : AppCompatButton = view.findViewById(R.id.btnMembership)
        btnMembership.setOnClickListener{
            checkMembershipStatus()
        }


        val btnSignOut = view.findViewById<Button>(R.id.btnUserignOut)
        btnSignOut.setOnClickListener {
            showSignOutDialog()
        }

        return view
    }

    private fun goToEditProfile() {
        val intent = Intent(requireContext(), EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun goToMyBookingsPage() {
        val intent = Intent(requireContext(), MyBookingsActivity::class.java)
        startActivity(intent)
    }

    private fun goToMembershipPage(){
        val intent = Intent(requireContext(), MembershipActivity::class.java)
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

    private fun checkMembershipStatus() {

        val userId = auth.currentUser?.uid

        if ( userId != null) {

            val membershipStatusRef = database.child(userId).child("membershipDetails").child("membershipStatus")

            membershipStatusRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val membershipStatus = snapshot.value.toString()

                    if (membershipStatus == "active") {
                        Toast.makeText(requireContext(), "You already have an active membership!", Toast.LENGTH_SHORT).show()
                    } else {
                        goToMembershipPage()
                    }
                } else {
                    goToMembershipPage()
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showSignOutDialog() {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_sign_out,null)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.signOutCancel)
        val btnConfirmSignOut = dialogView.findViewById<MaterialButton>(R.id.signOutConfirm)

        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirmSignOut.setOnClickListener {
            alertDialog.dismiss()
            signOutUser()
        }

    }

}
