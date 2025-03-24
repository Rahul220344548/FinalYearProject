package com.example.gym_application.utils

import android.os.Build
import androidx.annotation.RequiresApi

object ValidationClassScheduleFields {

    @RequiresApi(Build.VERSION_CODES.O)
    fun validationClassScheduleFields(
        selectedClassName : String,
        selectedRoom : String,
        selectedInstructor : String,
        startTime: String,
        endTime: String,
        startDate: String
    ): String {

        if (!ValidationClassCreation.isValidSelectClassOptionName(selectedClassName)) {
            return "Please select an Class Option"
        }
        if (!ValidationClassCreation.isValidSelectRoom(selectedRoom)) {
            return "Please select a Room"
        }
        if (!ValidationClassCreation.isValidSelectInstructor(selectedInstructor)) {
            return "Please select an Instructor"
        }
        if (!ValidationClassCreation.isValidTime(startTime, endTime)) {
            return "Invalid time: Class duration must be 30 minutes or 1 hour"
        }

        if (!ValidationClassCreation.isValidScheduledDate(startDate)) {
            return "Please pick a schedule date"
        }

        if (!ValidationClassCreation.isScheduleTimeInFuture(startDate, startTime)) {
            return "Cannot schedule a class in the past!"
        }

        return ""
    }

}