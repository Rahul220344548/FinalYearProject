package com.example.gym_application.controller

import android.util.Log
import android.widget.Toast
import com.example.gym_application.model.ClassModel
import com.example.gym_application.model.UserClassBooking
import com.example.gym_application.model.UserDetails
import com.google.firebase.database.*


class UserFirebaseDatabaseHelper {

    fun fetchUserCurrentBookings(userId : String, callback: (List<String>?) -> Unit) {

        val userDatabase = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)
            .child("bookings")
            .child("current")

        userDatabase.get().addOnSuccessListener { snapshot ->

            val classIdList = mutableListOf<String>()
            if (snapshot.exists()) {
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(UserClassBooking::class.java)
                    val fetchedclassId = booking?.classId

                    if (fetchedclassId != null) {
                        classIdList.add(fetchedclassId)
                    }
                }
                callback(classIdList)
            }


        }.addOnFailureListener { exception ->
            callback(null)
        }

    }

    fun fetchInstructors (callback: (List<String>) -> Unit) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val instructorList = mutableListOf<String>()

        databaseReference.get().addOnSuccessListener { snapshot ->
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
                callback(instructorList)
            }else {
                callback(emptyList())
            }
        }.addOnFailureListener {
            callback(emptyList())
        }

    }

    fun listenForUserUpdates(onDataChanged: (List<UserDetails>) -> Unit) {
        val userDatabase = FirebaseDatabase.getInstance().reference.child("users")

        userDatabase.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<UserDetails>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserDetails::class.java)
                    if (user != null && user.role != "Admin") {
                        Log.d("User Fetch", "Fetched user name: ${user.firstName} ${user.lastName}")
                        userList.add(user)
                    }
                }
                onDataChanged(userList)
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error Fetching Classes: ${error.message}")
            }
        })


    }



}