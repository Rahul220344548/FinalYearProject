package com.example.gym_application.controller

import android.util.Log
import android.widget.Toast
import com.example.gym_application.model.ClassModel
import com.example.gym_application.model.MembershipInfo
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

    fun listenForUserUpdates(onDataChanged: (Map<UserDetails, String>) -> Unit) {
        val userDatabase = FirebaseDatabase.getInstance().reference.child("users")
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMap = mutableMapOf<UserDetails, String>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserDetails::class.java)
                    val userId = userSnapshot.key
                    if (user != null && userId != null && user.role != "Admin") {
                        Log.d("User Fetch", "Fetched user name: ${user.firstName} ${user.lastName}")
                        userMap[user] = userId
                    }
                }
                onDataChanged(userMap)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error Fetching Users: ${error.message}")
            }
        })
    }

    fun adminUpdateUserInfo(userId:String, userUpdate: Map<String,Any>, callback: (Boolean) -> Unit) {

        val userDatabase = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)

        userDatabase.updateChildren(userUpdate)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun fetchUserMembershipInfo(userId: String, callback: (MembershipInfo?) -> Unit) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val membershipRef = databaseReference.child(userId).child("membershipDetails")

        membershipRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val membershipTitle = snapshot.child("membershipTitle").value?.toString() ?: "No Plan"
                val membershipDuration = snapshot.child("membershipDuration").value?.toString() ?: "Unknown"
                val membershipStatus = snapshot.child("membershipStatus").value?.toString()?.lowercase() ?: "inactive"
                val expirationDate = snapshot.child("endDate").value?.toString() ?: "Unknown"

                val membershipInfo = MembershipInfo(
                    membershipTitle,
                    membershipDuration,
                    membershipStatus,
                    expirationDate
                )
                callback(membershipInfo)
            }else {
                callback(null)
            }
        }.addOnFailureListener { error ->
            callback(null)
        }

    }

    fun deleteUserAuthetication() {

    }

    fun deleteUserData() {

    }

}