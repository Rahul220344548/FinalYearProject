package com.example.gym_application.controller
import android.util.Log
import android.widget.Toast
import com.example.gym_application.model.UserClassBooking
import com.google.firebase.database.*
class UserFirebaseDatabaseHelper {

    fun getUserCurrentBookings(userId : String, callback: (String?) -> Unit) {

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
            }


        }.addOnFailureListener { exception ->
            callback(null)
        }

    }
}