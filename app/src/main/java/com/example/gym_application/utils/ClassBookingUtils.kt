package com.example.gym_application.utils

import FirebaseDatabaseHelper
import android.content.Context
import android.view.View
import android.widget.AutoCompleteTextView
import com.example.gym_application.R
import com.example.gym_application.adapter.UserFirebaseDatabaseHelper
import com.example.gym_application.newModel.Instructor

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

    fun setUpSelectClassColordropdown(
        context: Context,
        autoCompleteColorTextView: AutoCompleteTextView,
        onColorSelected: (String) -> Unit
        ){
        utilsSetUpSelectColorDropdown(context, autoCompleteColorTextView, onColorSelected)
    }

    fun setUpSelectAvailabilityFordropdown(
        context: Context,
        autoCompleteclassAvailabilityFor: AutoCompleteTextView,
        onAvailabilitySelected: (String) -> Unit
    ) {
        utilsSetUpSelectAvailabilityForDropdown(context, autoCompleteclassAvailabilityFor, onAvailabilitySelected)
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

    fun setUpSelectClassesOptionsDropdown(
        context: Context,
        dialogView: View,
        onMapReady: (Map<String, String>) -> Unit
        ) {
        val classOption = dialogView.findViewById<AutoCompleteTextView>(R.id.select_class_for_schedule)
        val classFirebaseHelper = FirebaseDatabaseHelper()

        classFirebaseHelper.fetchClassName { classMap  ->

            val classTitles = classMap.keys.toList()

            utilsSetUpSelectClassesOptionsDropdown(
                context = context,
                classList = classTitles,
                classOption = classOption,
                selectedClasses = { selected ->
                }
            )
            onMapReady(classMap)

        }
    }

    fun setUpSelectRoomdropdown(
        context: Context,
        dialogView: View,
        onRoomSelected: (String) -> Unit
    ) {
        val autoCompleteRoomView = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_schedule_room)
        utilsSetUpSelectRoomDropdown(context,autoCompleteRoomView, onRoomSelected)
    }

    fun setUpSelectInstructordropdown(
        context: Context,
        dialogView: View,
        selectedInstructor: (Instructor) -> Unit
    ){
        val userFirebaseHelper = UserFirebaseDatabaseHelper()
        val autoCompleteInstructorView = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_schedule_instructor)

        userFirebaseHelper.fetchInstructors { instructorList ->
            utilsSetUpSelectInstructorDropdown(
                context = context,
                instructorList = instructorList,
                autoCompleteInstructorView,
                selectedInstructor = selectedInstructor
            )
        }
    }

}