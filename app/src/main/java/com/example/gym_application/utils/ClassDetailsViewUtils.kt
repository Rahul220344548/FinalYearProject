package com.example.gym_application.utils

import FirebaseDatabaseHelper
import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.gym_application.R
import com.example.gym_application.adapter.ScheduleFirebaseHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val classFirebaseHelper = FirebaseDatabaseHelper()
val scheduleFirebaseHelper = ScheduleFirebaseHelper()

fun utilsSetUpClassTitle(activity: Activity, classTitle: String?) {
    val txtClassTitle = activity.findViewById<TextView>(R.id.classTitle)
    txtClassTitle.text = classTitle?.uppercase()
}

@RequiresApi(Build.VERSION_CODES.O)
fun utilsSetUpClassStartDate(activity: Activity,inClassStartDate: String) {

    val txtClassScheduledDate = activity.findViewById<TextView>(R.id.classScheduledDate)
    val formatDate = ValidationClassCreation.formatDate(inClassStartDate)
    txtClassScheduledDate.text = formatDate
}

fun utilsSetUpClassStartTime(activity: Activity,inClassStartTime: String,inClassEndTime: String){

    val txtClassScheduledTime = activity.findViewById<TextView>(R.id.classScheduledTime)
    txtClassScheduledTime.text = "$inClassStartTime - $inClassEndTime"
}

fun utilsSetUpClassDuration(activity: Activity,inClassStartTime: String,inClassEndTime: String) {

    val startMinutes = ValidationClassCreation.convertTimeToMinutes(inClassStartTime)
    val endMinutes = ValidationClassCreation.convertTimeToMinutes(inClassEndTime)
    val duration = endMinutes - startMinutes

    val txtclassLength = activity.findViewById<TextView>(R.id.classLength)
    txtclassLength.text = "$duration min"
}

fun utilsSetUpClassAvailableFor(activity: Activity,classAvailabilityFor: String) {

    val txtclassAvailability = activity.findViewById<TextView>(R.id.classAvailabilityFor)
    if (classAvailabilityFor == "All") {
        txtclassAvailability.text = classAvailabilityFor?.toUpperCase()
    }else {
        txtclassAvailability.text = "$classAvailabilityFor only"?.toUpperCase()
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun utilsSetUpClassStatus(activity: Activity,
                          maxCapacity: Int,
                          currBookings : Int,
                          status: String) {

    val remainingSpots = maxCapacity - currBookings
    val txtClassRemainingSpot =activity.findViewById<TextView>(R.id.classRemainingSpots)
    val btnBookClass = activity.findViewById<Button>(R.id.btnBookClass)

    if (isClassOver(activity)) {
        txtClassRemainingSpot.text = "No Longer Available"
        txtClassRemainingSpot.setTextColor(ContextCompat.getColor(activity, R.color.red))
        return
    }

    if (status == "inactive") {
        txtClassRemainingSpot.text = "Class Cancelled"
        txtClassRemainingSpot.setTextColor(ContextCompat.getColor(activity, R.color.red))
        return
    }

    if (currBookings >= maxCapacity) {
        txtClassRemainingSpot.setTextColor(ContextCompat.getColor(activity,R.color.red))
        txtClassRemainingSpot.text = "Class Full"
        btnBookClass.visibility = View.GONE
        return
    }
    txtClassRemainingSpot.text = "Available ($remainingSpots spots left)"
    txtClassRemainingSpot.setTextColor(ContextCompat.getColor(activity, R.color.available_green))
    btnBookClass.isEnabled = true

}

fun utilsSetUpClassLocation(activity: Activity, inClassLocation: String) {

    val txtClassLocation = activity.findViewById<TextView>(R.id.classLocation)
    txtClassLocation.text = inClassLocation
}

fun utilsSetUpClassInstructor(activity: Activity, inClassInstructor: String) {
    val txtClassInstructor =activity.findViewById<TextView>(R.id.classInstructor)
    txtClassInstructor.text = inClassInstructor
}

fun utilsSetUpClassDescription(activity: Activity,inClassDescription: String) {

    val txtClassDescription = activity.findViewById<TextView>(R.id.classDescription)
    txtClassDescription.text = inClassDescription
}

@RequiresApi(Build.VERSION_CODES.O)
fun utilsSetUpBookAndCancelButton(activity: Activity, userId : String,
                                  scheduleId : String, status: String) {
    val btnBookClass = activity.findViewById<Button>(R.id.btnBookClass)
    val bookingConfirmTextView = activity.findViewById<TextView>(R.id.bookingConfirmationText)
    val btnCancelClass = activity.findViewById<Button>(R.id.btnCancelClassBooking)

    if (isClassOver(activity)) {
        btnBookClass.visibility = View.GONE
        bookingConfirmTextView.text = "CLASS COMPLETED"
//        bookingConfirmTextView.visibility = View.GONE
        btnCancelClass.visibility = View.GONE
        return
    }

    if (status=="inactive") {
        btnBookClass.visibility = View.GONE
        btnCancelClass.visibility = View.GONE
        return
    }

    classFirebaseHelper.hasUserAlreadyBookedThisClass(userId,scheduleId ?: "") { isBooked ->
        if (isBooked) {
            // show confirmation and cancel button
            bookingConfirmTextView.visibility = View.VISIBLE
            btnBookClass.visibility = View.GONE
            btnCancelClass.visibility = View.VISIBLE
        } else {
            btnBookClass.isClickable = true
            btnBookClass.isEnabled = true
            btnBookClass.text = "Book Class"
        }
    }
}

fun utilsValidateUserMembershipAndGender(
    activity: Activity,
    classAvailabilityFor: String,
    userId: String,
    onEligibleToBook: () -> Unit
) {

    classFirebaseHelper.getUserMembershipStatus(userId) { membershipStatus ->
        if ( membershipStatus != "active") {
            Toast.makeText(activity, "Activate your membership to book this class!"
                , Toast.LENGTH_LONG).show()
            return@getUserMembershipStatus
        }
        classFirebaseHelper.getUserGender(userId) { userGender ->
            if (userGender != null) {
                if (ClassBookingUtils.canUserBookClass(classAvailabilityFor,userGender)) {
                    onEligibleToBook()
                    return@getUserGender
                } else {
                    Toast.makeText(activity, "This class is only available for $classAvailabilityFor participants."
                        , Toast.LENGTH_LONG).show()
                    return@getUserGender
                }
            } else {
                println("Unable to fetch user gender. Please try again.")
            }
        }
    }


}

fun utilsUpdateClassCurrentBookings(
    scheduleId : String,
    currentBookingCount : Map<String,Any>,
    callback: (Boolean) -> Unit) {

    scheduleFirebaseHelper.newIncrementClassCurrentBookingInSchedules(
        scheduleId,
        currentBookingCount,
        callback
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isClassOver(activity: Activity): Boolean {
    val inClassDate = activity.intent.getStringExtra("classStartDate") ?: return true
    val inClassEndTime = activity.intent.getStringExtra("classEndTime") ?: return true

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    return try {
        val classDate = LocalDate.parse(inClassDate, dateFormatter)
        val classEndTime = LocalTime.parse(inClassEndTime, timeFormatter)
        val classDateTimeEnd = classDate.atTime(classEndTime)

        val now = LocalDateTime.now()
        now.isAfter(classDateTimeEnd)
    } catch (e: Exception) {
        true
    }
}

