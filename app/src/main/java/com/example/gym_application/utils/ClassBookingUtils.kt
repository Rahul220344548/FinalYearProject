package com.example.gym_application.utils

import android.widget.Toast

object ClassBookingUtils {

    fun handleUserMembershipStatus(membershipStatus: String) {
        if ( membershipStatus != "active") {
//            Toast.makeText(this, "$membershipStatus Class is not avaaliable for you.", Toast.LENGTH_LONG).show()
            return
        }
    }

    fun canUserBookClass(classAvailableFor: String, userGender: String): Boolean {
        return when (classAvailableFor) {
            "All" -> true  // Everyone can book
            "Female" -> userGender == "Female" // Only females can book
            "Male" -> userGender == "Male" // Only males can book
            else -> false // If classAvailableFor is unknown, deny booking
        }
    }


}