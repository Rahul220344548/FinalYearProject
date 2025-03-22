package com.example.gym_application.utils

import FirebaseDatabaseHelper
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.gym_application.R
import com.google.android.material.button.MaterialButton

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

    fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return (hours * 60) + minutes
    }

    fun setUpStartTimeDropdown(context: Context, dialogView: View) {
        val autoCompleteStartTime = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_startTime)
        utilsSetUpStartTimeDropdown(context, autoCompleteStartTime)
    }

    fun setUpEndTimeDropdown(context: Context, dialogView: View) {
        val autoCompleteEndTime = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_endTime)
        utilsSetUpEndTimeDropdown(context, autoCompleteEndTime)
    }

    fun setUpStartDate(context: Context, dialogView: View) {
        val startDate = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_starDate)
        utilsSetUpClassScheduleDate(context, startDate)
    }

    fun setUpSelectClassesOptionsDropdown(context: Context, dialogView: View) {
        val classOption = dialogView.findViewById<AutoCompleteTextView>(R.id.select_class_for_schedule)
        val classFirebaseHelper = FirebaseDatabaseHelper()

        classFirebaseHelper.fetchClassName { classList ->
            utilsSetUpSelectClassesOptionsDropdown(
                context = context,
                classList = classList,
                classOption = classOption,
                selectedClasses = { selected ->
                }
            )
        }
    }


}